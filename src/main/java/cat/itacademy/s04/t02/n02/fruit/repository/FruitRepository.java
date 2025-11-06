package cat.itacademy.s04.t02.n02.fruit.repository;

import cat.itacademy.s04.t02.n02.fruit.model.Fruit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FruitRepository extends JpaRepository<Fruit, Long> {
    List<Fruit> findBySupplierId(Long supplierId);
}
