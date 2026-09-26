package com.logistics.global.reference;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/** 자기 도메인 엔티티의 ID 필드를 JPQL로 조회하는 ReferenceChecker 공통 부모 */
public abstract class JpqlReferenceChecker implements ReferenceChecker {

    @PersistenceContext
    private EntityManager em;

    protected boolean exists(String entity, String field, Long id) {
        return em.createQuery(
                        "SELECT COUNT(e) > 0 FROM " + entity + " e WHERE e." + field + " = :id", Boolean.class)
                .setParameter("id", id)
                .getSingleResult();
    }
}
