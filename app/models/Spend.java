package models;

import play.data.format.Formats;
import play.data.validation.Constraints;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import java.util.Date;

@Entity
public class Spend extends BaseModel {
    private static final long serialVersionUID = 1L;

    @Constraints.Required
    private String name;

    @Formats.DateTime(pattern="yyyy-MM-dd")
    private Date spentDate;

    private Double spentValue;

    public void update(Spend newSpendData) {
        setName(newSpendData.name);
        setSpentDate(newSpendData.spentDate);
        setSpentValue(newSpendData.spentValue);
        update();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getSpentDate() {
        return spentDate;
    }

    public void setSpentDate(Date spentDate) {
        this.spentDate = spentDate;
    }

    public Double getSpentValue() {
        return spentValue;
    }

    public void setSpentValue(Double spentValue) {
        this.spentValue = spentValue;
    }
}
