package com.kakaohealthcare.adrepository.repository;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.io.Serializable;

/**
 * Description
 *
 * @author yohan.an
 * @version Copyright (C) 2024 by KakaoHealthcare. All right reserved.
 * @since 2024. 7. 1.
 */


@Builder
@Entity
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class CampaignEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long campaignId;

    String campaignName;

    public CampaignEntity() {

    }


}
