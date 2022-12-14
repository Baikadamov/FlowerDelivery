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

  @Column(name = "description" ,columnDefinition = "TEXT")
  private String description    ;

  @Column(name = "price")
  private int price  ;

  @Column(name = "composition")
  private String composition   ;


  @ManyToOne
  private Status status;


}
