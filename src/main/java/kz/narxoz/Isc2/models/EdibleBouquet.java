package kz.narxoz.Isc2.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "t_edible_bouquet")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EdibleBouquet {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "name")
  private String name;

  @Column(name = "image")
  private String image;

  @Column(name = "description", columnDefinition="TEXT", length=10485760)
  private String description    ;

  @Column(name = "price")
  private int price  ;

  @Column(name = "composition")
  private String composition   ;
}
