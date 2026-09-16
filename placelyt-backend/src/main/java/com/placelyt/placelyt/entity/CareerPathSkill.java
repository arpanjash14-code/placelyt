package com.placelyt.placelyt.entity;

import jakarta.persistence.*;

@Entity
@Table(
    name = "career_path_skills",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"career_path_id", "skill_id"})
    }
)
public class CareerPathSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "career_path_id", nullable = false)
    private CareerPath careerPath;

    @ManyToOne
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    public CareerPathSkill() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CareerPath getCareerPath() {
        return careerPath;
    }

    public void setCareerPath(CareerPath careerPath) {
        this.careerPath = careerPath;
    }

    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }
}