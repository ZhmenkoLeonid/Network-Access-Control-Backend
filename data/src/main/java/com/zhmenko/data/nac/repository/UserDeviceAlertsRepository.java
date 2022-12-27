package com.zhmenko.data.nac.repository;


import com.zhmenko.data.nac.models.UserDeviceAlertEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDeviceAlertsRepository extends CrudRepository<UserDeviceAlertEntity,Integer> {

}
