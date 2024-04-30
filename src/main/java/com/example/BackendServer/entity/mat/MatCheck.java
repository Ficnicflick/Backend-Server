package com.example.BackendServer.entity.mat;

import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Getter;

@Embeddable @Getter
public class MatCheck {

    private int useCount;
    private MatStatus matStatus;

    @Builder
    public MatCheck() {
        this.useCount = 0;
        this.matStatus = MatStatus.AVAILABLE;
    }

    public void countPlus(){
        this.useCount++;
    }
    public void changeMatStatus(MatStatus matStatus){
        this.matStatus = matStatus;
    }
    public void changeMatCount(int useCount){
        this.useCount = useCount;
    }

}
