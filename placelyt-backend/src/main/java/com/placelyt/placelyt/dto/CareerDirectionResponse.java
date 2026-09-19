package com.placelyt.placelyt.dto;

public class CareerDirectionResponse {

    private CareerPathResponse currentDirection;
    private CareerPathResponse targetDirection;

    public CareerDirectionResponse() {}

    public CareerDirectionResponse(
            CareerPathResponse currentDirection,
            CareerPathResponse targetDirection) {

        this.currentDirection = currentDirection;
        this.targetDirection = targetDirection;
    }

    public CareerPathResponse getCurrentDirection() {
        return currentDirection;
    }

    public void setCurrentDirection(
            CareerPathResponse currentDirection) {

        this.currentDirection = currentDirection;
    }

    public CareerPathResponse getTargetDirection() {
        return targetDirection;
    }

    public void setTargetDirection(
            CareerPathResponse targetDirection) {

        this.targetDirection = targetDirection;
    }
}