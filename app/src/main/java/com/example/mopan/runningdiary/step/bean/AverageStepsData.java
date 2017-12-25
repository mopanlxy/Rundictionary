package com.example.mopan.runningdiary.step.bean;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

/**
 * Created by mopan on 2017/10/16.
 */
@Table("averagesteps")
public class AverageStepsData {
    // 指定自增，每个对象需要有一个主键
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    private int id;

    @Column("month")
    private String month;
    @Column("step")
    private String step;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }


    @Override
    public String toString() {
        return "StepData{" +
                "id=" + id +
                ", month='" + month + '\'' +
                ", step='" + step + '\'' +
                '}';
    }





}
