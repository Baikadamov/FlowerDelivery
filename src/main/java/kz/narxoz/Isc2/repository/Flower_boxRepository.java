package kz.narxoz.Isc2.repository;

import kz.narxoz.Isc2.models.Bouquets;
import kz.narxoz.Isc2.models.FlowerInBox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface Flower_boxRepository extends JpaRepository<FlowerInBox,Long> {

  @Query("SELECT b from FlowerInBox b where " + "concat(b.price , b.name , b.composition ) " + "like %?1%")
  public List<FlowerInBox> findAll(String keyword);

  @Query("SELECT b    from FlowerInBox b  where b.price >= ?1 and b.price <= ?2 ")
  public List<FlowerInBox> findAll(Integer keyword, Integer keyword2);
}

