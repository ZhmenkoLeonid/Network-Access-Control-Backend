package com.zhmenko.data.nac.repository;


import com.zhmenko.data.nac.models.BlackListEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlackListRepository extends CrudRepository<BlackListEntity, String> {
    @Query(value = "select user_banned from nac_user_blacklist where mac_address = ?1", nativeQuery = true)
    boolean findIsBlockedByMacAddress(String macAddress);
}
