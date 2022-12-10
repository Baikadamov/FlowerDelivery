package kz.narxoz.Isc2.repository;

import kz.narxoz.Isc2.models.Bouquets;
import kz.narxoz.Isc2.models.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface StatusRepository extends JpaRepository<Status,Long> {


}
