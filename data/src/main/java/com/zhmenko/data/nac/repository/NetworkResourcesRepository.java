package com.zhmenko.data.nac.repository;


import com.zhmenko.data.nac.entity.NetworkResourceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NetworkResourcesRepository extends JpaRepository<NetworkResourceEntity, Integer> {

}
