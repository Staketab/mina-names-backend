package com.staketab.minanames.service.impl;

import com.staketab.minanames.dto.ApplyReservedDomainDTO;
import com.staketab.minanames.dto.CartReservedDomainDTO;
import com.staketab.minanames.dto.DomainCartReservationDTO;
import com.staketab.minanames.dto.DomainCartReserveUpdateDTO;
import com.staketab.minanames.dto.DomainDTO;
import com.staketab.minanames.dto.DomainReservationDTO;
import com.staketab.minanames.dto.DomainUpdateDTO;
import com.staketab.minanames.dto.OldMetadataDTO;
import com.staketab.minanames.dto.ReservedDomainDTO;
import com.staketab.minanames.dto.request.BaseRequest;
import com.staketab.minanames.dto.request.SearchParams;
import com.staketab.minanames.entity.DomainEntity;
import com.staketab.minanames.entity.DomainStatus;
import com.staketab.minanames.entity.PayableTransactionEntity;
import com.staketab.minanames.entity.TxStatus;
import com.staketab.minanames.exception.DuplicateKeyException;
import com.staketab.minanames.exception.NotFoundException;
import com.staketab.minanames.repository.DomainRepository;
import com.staketab.minanames.service.ActivityService;
import com.staketab.minanames.service.DomainService;
import com.staketab.minanames.service.TxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.staketab.minanames.entity.ActivityStatus.APPLY_CART_RESERVED_DOMAINS;
import static com.staketab.minanames.entity.ActivityStatus.CART_RESERVE;
import static com.staketab.minanames.entity.ActivityStatus.CREATE;
import static com.staketab.minanames.entity.ActivityStatus.DELETE_CART_RESERVE;
import static com.staketab.minanames.entity.ActivityStatus.REMOVE_CART_RESERVATION;
import static com.staketab.minanames.entity.ActivityStatus.REMOVE_RESERVATION;
import static com.staketab.minanames.entity.DomainStatus.PENDING;
import static com.staketab.minanames.entity.DomainStatus.RESERVED;
import static com.staketab.minanames.utils.Constants.DEFAULT_DENOMINATION;
import static com.staketab.minanames.utils.Constants.MINA_DENOMINATION;

@Service
@RequiredArgsConstructor
@Slf4j
public class DomainServiceImpl implements DomainService {

    private final ActivityService activityService;
    private final DomainRepository domainRepository;
    private final TxService txService;

    @Override
    public Page<DomainEntity> findAllByPageable(BaseRequest request, SearchParams searchParams) {
        return domainRepository.findAllDomains(searchParams.getSearchStr(), request.buildPageable());
    }

    @Override
    public Page<DomainEntity> findAllByAccountPageable(BaseRequest request, String accountAddress, SearchParams searchParams) {
        return domainRepository.findAllDomainsByAccount(searchParams.getSearchStr(), accountAddress, request.buildPageable());
    }

    @Override
    public DomainEntity create(DomainReservationDTO request) {
        String domainName = request.getDomainName();
        domainRepository.findDomainEntityByDomainName(domainName)
                .ifPresent(domainEntity -> {
                    throw new DuplicateKeyException(String.format("Domain already exist with name: %s", domainName));
                });
        DomainEntity domain = buildDomainEntity(request);
        activityService.saveActivity(domain, CREATE);
        return domainRepository.save(domain);
    }

    @Override
    @Transactional
    public DomainEntity reserve(DomainCartReservationDTO request) {
        String domainName = request.getDomainName();
        domainRepository.findDomainEntityByDomainName(domainName)
                .ifPresent(domainEntity -> {
                    throw new DuplicateKeyException(String.format("Domain already exist with name: %s", domainName));
                });
        DomainEntity domain = buildDomainEntityReserve(request);
        activityService.saveActivity(domain, CART_RESERVE);
        DomainEntity saved = domainRepository.save(domain);
        List<DomainEntity> domainEntities = domainRepository.findAllCartsReservedDomains(saved.getId())
                .stream()
                .peek(domainEntity -> domainEntity.setReservationTimestamp(saved.getReservationTimestamp())).toList();
        domainRepository.saveAll(domainEntities);
        return saved;
    }

    @Override
    @Transactional
    public void updateReserve(DomainCartReserveUpdateDTO domainRequest) {
        long currentTimeMillis = System.currentTimeMillis();
        List<DomainEntity> reservedDomains = domainRepository.findAllCartsReservedDomains(domainRequest.getId());
        for (DomainEntity reservedDomain : reservedDomains) {
            if (reservedDomain.getId().equals(domainRequest.getId())) {
                reservedDomain.setExpirationTime(domainRequest.getExpirationTime());
                reservedDomain.setAmount(Math.round(domainRequest.getAmount() * MINA_DENOMINATION));
            }
            reservedDomain.setReservationTimestamp(currentTimeMillis);
        }
        domainRepository.saveAll(reservedDomains);
    }

    @Override
    @Transactional
    public void applyReservedDomain(ApplyReservedDomainDTO domainRequest) {
        Map<String, CartReservedDomainDTO> cartDomainMap = domainRequest.getDomains()
                .stream()
                .collect(Collectors.toMap(CartReservedDomainDTO::getDomainName, Function.identity()));
        PayableTransactionEntity payableTransaction = txService.getOrCreate(domainRequest.getTxHash(), domainRequest.getDomains().size(), TxStatus.PENDING);
        List<DomainEntity> domains = domainRepository.findAllByOwnerAddressAndDomainNameIn(domainRequest.getOwnerAddress(), cartDomainMap.keySet());
        List<String> txHashes = domains.stream()
                .map(DomainEntity::getTransaction)
                .map(PayableTransactionEntity::getTxHash)
                .toList();
        saveUpdatedDomains(domains, cartDomainMap, payableTransaction);
        txService.deleteTxs(txHashes);
    }

    @Override
    public void removeReservedDomain(String id) {
        domainRepository.findById(id).ifPresent(domainEntity -> {
            activityService.saveActivity(domainEntity, DELETE_CART_RESERVE);
            domainRepository.delete(domainEntity);
        });
    }

    @Override
    public DomainDTO retrieve(String id) {
        return domainRepository.findById(id)
                .map(this::buildDomainDTO)
                .orElseThrow(() -> new NotFoundException(String.format("Domain isn’t found by id: %s", id)));
    }

    @Override
    public DomainEntity update(DomainUpdateDTO domainUpdateDTO) {
        return domainRepository.findById(domainUpdateDTO.getId())
                .map(domainEntity -> {
                    domainEntity.setDomainImg(domainUpdateDTO.getImg());
                    return domainRepository.save(domainEntity);
                })
                .orElseThrow(() -> new NotFoundException("Domain not found by id: " + domainUpdateDTO.getId()));
    }

    @Override
    public ReservedDomainDTO isNameReserved(String name) {
        return domainRepository.findDomainEntityByDomainName(name)
                .map(this::mapToReservedDomainDTO)
                .orElseGet(ReservedDomainDTO::new);
    }

    @Override
    @Transactional
    public Boolean setDefaultDomain(String id) {
        return domainRepository.setDefaultDomain(id) > 0;
    }

    @Override
    @Transactional
    public void removeReservedDomains() {
        LocalDateTime localDateTime = LocalDateTime.now().minusDays(1);
        long currentTimestamp = Timestamp.valueOf(localDateTime).getTime();
        List<DomainEntity> domainEntities = domainRepository.findAllByReservationTimestampLessThan(currentTimestamp, PENDING.name());
        activityService.saveAllActivities(domainEntities, REMOVE_RESERVATION);
        domainRepository.deleteAll(domainEntities);
    }

    @Override
    public void removeCartReservedDomains() {
        LocalDateTime localDateTime = LocalDateTime.now().minusMinutes(10);
        long currentTimestamp = Timestamp.valueOf(localDateTime).getTime();
        List<DomainEntity> domainEntities = domainRepository.findAllByReservationTimestampLessThan(currentTimestamp, RESERVED.name());
        activityService.saveAllActivities(domainEntities, REMOVE_CART_RESERVATION);
        domainRepository.deleteAll(domainEntities);
    }

    private void saveUpdatedDomains(List<DomainEntity> domains, Map<String, CartReservedDomainDTO> cartDomainMap, PayableTransactionEntity payableTransaction) {
        LocalDateTime now = LocalDateTime.now();
        List<DomainEntity> domainEntities = domains
                .stream()
                .peek(domainEntity -> {
                    CartReservedDomainDTO cartReservedDomainDTO = cartDomainMap.get(domainEntity.getDomainName());
                    long endTimestamp = Timestamp.valueOf(now.plusYears(domainEntity.getExpirationTime())).getTime();
                    domainEntity.setAmount(Math.round(cartReservedDomainDTO.getAmount() * MINA_DENOMINATION));
                    domainEntity.setTransaction(payableTransaction);
                    domainEntity.setDomainStatus(PENDING.name());
                    domainEntity.setEndTimestamp(endTimestamp);
                }).toList();
        activityService.saveAllActivities(domainEntities, APPLY_CART_RESERVED_DOMAINS);
        domainRepository.saveAll(domainEntities);
    }

    private ReservedDomainDTO mapToReservedDomainDTO(DomainEntity domainEntity) {
        return ReservedDomainDTO.builder()
                .id(domainEntity.getId())
                .status(DomainStatus.valueOf(domainEntity.getDomainStatus()))
                .build();
    }

    private DomainEntity buildDomainEntity(DomainReservationDTO request) {
        PayableTransactionEntity tx = txService.getOrCreate(request.getTxHash(), 1, TxStatus.PENDING);
        return DomainEntity.builder()
                .ownerAddress(request.getOwnerAddress())
                .transaction(tx)
                .domainName(request.getDomainName())
                .amount(Math.round(request.getAmount() * MINA_DENOMINATION))
                .expirationTime(request.getExpirationTime())
                .blockNumber(0)
                .endTimestamp(Timestamp.valueOf(LocalDateTime.now().plusYears(request.getExpirationTime())).getTime())
                .reservationTimestamp(System.currentTimeMillis())
                .domainStatus(PENDING.name())
                .isSendToCloudWorker(false)
                .isDefault(false)
                .build();
    }

    private DomainEntity buildDomainEntityReserve(DomainCartReservationDTO request) {
        PayableTransactionEntity tx = txService.getOrCreate(String.format("%s_%s", request.getDomainName(), RESERVED.name()), 1, TxStatus.RESERVED);
        return DomainEntity.builder()
                .ownerAddress(request.getOwnerAddress())
                .domainName(request.getDomainName())
                .transaction(tx)
                .amount(Math.round(request.getAmount() * MINA_DENOMINATION))
                .expirationTime(request.getExpirationTime())
                .blockNumber(0)
                .endTimestamp(Timestamp.valueOf(LocalDateTime.now().plusYears(request.getExpirationTime())).getTime())
                .reservationTimestamp(System.currentTimeMillis())
                .domainStatus(RESERVED.name())
                .isSendToCloudWorker(false)
                .isDefault(false)
                .build();
    }

    private DomainDTO buildDomainDTO(DomainEntity domainEntity) {
        return DomainDTO.builder()
                .id(domainEntity.getId())
                .domainName(domainEntity.getDomainName())
                .amount(BigDecimal.valueOf(domainEntity.getAmount()).divide(DEFAULT_DENOMINATION, RoundingMode.HALF_UP))
                .startTimestamp(domainEntity.getStartTimestamp())
                .endTimestamp(domainEntity.getEndTimestamp())
                .domainStatus(DomainStatus.valueOf(domainEntity.getDomainStatus()))
                .expirationTime(domainEntity.getExpirationTime())
                .isDefault(domainEntity.getIsDefault())
                .ipfs(domainEntity.getIpfs())
                .isSendToCloudWorker(domainEntity.getIsSendToCloudWorker())
                .ownerAddress(domainEntity.getOwnerAddress())
                .reservationTimestamp(domainEntity.getReservationTimestamp())
                .transaction(domainEntity.getTransaction().getTxHash())
                .oldMetadata(buildOldMetadataDTO(domainEntity))
                .build();
    }

    private OldMetadataDTO buildOldMetadataDTO(DomainEntity domainEntity) {
        return OldMetadataDTO.builder()
                .xTwitter(domainEntity.getXTwitter())
                .email(domainEntity.getEmail())
                .description(domainEntity.getDescription())
                .discord(domainEntity.getDiscord())
                .github(domainEntity.getGithub())
                .website(domainEntity.getWebsite())
                .telegram(domainEntity.getTelegram())
                .ipfsImg(domainEntity.getIpfsImg())
                .domainMetadata(domainEntity.getDomainMetadata())
                .build();
    }
}
