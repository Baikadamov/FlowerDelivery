package kz.narxoz.Isc2.repository;

import kz.narxoz.Isc2.models.Bouquets;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface BouquetsRepository extends JpaRepository<Bouquets,Long> {


}
