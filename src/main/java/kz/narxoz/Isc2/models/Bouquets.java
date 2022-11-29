package kz.narxoz.Isc2.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "t_bouquets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bouquets {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "name")
  private String name;

  @Column(name = "b_image")
  private String b_image;

  @Column(name = "b_image2")
  private String b_image2;

  @Column(name = "b_image3")
  private String b_image3;


  @Column(name = "description")
  private String description    ;

  @Column(name = "price")
  private int price  ;

  @Column(name = "composition")
  private String composition   ;

  @Column(name = "size")
  private String size    ;


}
