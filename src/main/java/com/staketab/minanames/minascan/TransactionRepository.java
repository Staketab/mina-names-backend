package com.staketab.minanames.minascan;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
@ConditionalOnProperty(value = "spring.minascan-datasource.enabled", havingValue = "true")
public interface TransactionRepository extends JpaRepository<UserCommandsEntity, Integer> {

    @Query(nativeQuery = true,
            value = """
                    SELECT uc.hash as txHash,
                           buc.status as status,
                           (cast(uc.amount as bigint) + cast(uc.fee as bigint)) as amount
                    FROM user_commands uc
                             JOIN blocks_user_commands buc ON uc.id = buc.user_command_id
                             JOIN a_canonical_blocks cb ON cb.id = buc.block_id
                    WHERE uc.hash in (:txHash)
                       """)
    List<TxProjection> getTxStatusByTxHashIn(Collection<String> txHash);
}
