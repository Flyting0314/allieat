package com.backstage.backstagedto;

import java.util.List;

public class ChartDTO {
    private List labels;
    private List data;
    public ChartDTO() {}
    public ChartDTO(List labels, List data) {
        this.labels = labels;
        this.data = data;
    }

    public List getLabels() {
        return labels;
    }

    public void setLabels(List labels) {
        this.labels = labels;
    }

    public List getData() {
        return data;
    }

    public void setData(List data) {
        this.data = data;
    }
}
