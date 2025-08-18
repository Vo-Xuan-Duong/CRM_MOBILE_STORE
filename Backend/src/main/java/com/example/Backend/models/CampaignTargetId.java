package com.example.Backend.models;

import java.io.Serializable;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CampaignTargetId implements Serializable {

    private Long campaign;
    private Long customer;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CampaignTargetId that = (CampaignTargetId) o;
        return Objects.equals(campaign, that.campaign) &&
               Objects.equals(customer, that.customer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(campaign, customer);
    }
}
