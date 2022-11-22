package com.zhmenko.data.nac.repository;


import com.zhmenko.data.nac.models.NacUserAlertEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NacUserAlertsRepository extends CrudRepository<NacUserAlertEntity,Integer> {

}
