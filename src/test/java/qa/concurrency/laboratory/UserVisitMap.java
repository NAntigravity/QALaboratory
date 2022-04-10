package qa.concurrency.laboratory;

import lombok.Getter;
import lombok.Setter;

public class UserVisitMap {
    @Getter
    public String userName;

    @Getter
    public Integer visitsAmount;

    @Getter
    @Setter
    public Integer visitsRemain;

    public UserVisitMap(String userName, Integer visitsAmount) {
        this.userName = userName;
        this.visitsAmount = visitsAmount;
        this.visitsRemain = visitsAmount;
    }
}
