package com.zhmenko.data.nac.repository;


import com.zhmenko.data.nac.models.NetworkResourceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NetworkResourcesRepository extends JpaRepository<NetworkResourceEntity, Integer> {

}
