package io.ten1010.coaster.auth.web;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode
@ToString
public class ApiResourceList<T extends ApiResource> {

    @Nullable
    private Integer page;
    @Nullable
    private Integer limit;
    private List<T> items;

    public ApiResourceList() {
        this.items = new ArrayList<>();
    }

    public ApiResourceList<T> addItem(T item) {
        this.items.add(item);

        return this;
    }

}
