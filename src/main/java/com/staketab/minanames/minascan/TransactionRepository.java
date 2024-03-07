package com.staketab.minanames.minascan;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(value = "spring.minascan-datasource.enabled", havingValue = "true")
public interface TransactionRepository extends JpaRepository<UserCommandsEntity, Integer> {

    @Query(nativeQuery = true,
            value = """
                    SELECT buc.status as status
                    FROM user_commands uc
                             JOIN blocks_user_commands buc ON uc.id = buc.user_command_id
                             JOIN a_canonical_blocks cb ON cb.id = buc.block_id
                    WHERE uc.hash = :txHash
                       """)
    String getTxStatusByTxHash(String txHash);
}
