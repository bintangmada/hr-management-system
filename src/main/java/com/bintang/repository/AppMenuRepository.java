package com.bintang.repository;

import com.bintang.entity.AppMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AppMenuRepository extends JpaRepository<AppMenu, Long> {
    List<AppMenu> findByIsActiveOrderBySortOrderAsc(Boolean isActive);
    List<AppMenu> findByParentIdOrderBySortOrderAsc(Long parentId);
}
