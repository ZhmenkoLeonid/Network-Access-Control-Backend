package com.zhmenko.web.nac.data.repository;


import com.zhmenko.ids.data.nac.entity.NetworkResourceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NetworkResourcesRepository extends JpaRepository<NetworkResourceEntity, Integer> {

}
