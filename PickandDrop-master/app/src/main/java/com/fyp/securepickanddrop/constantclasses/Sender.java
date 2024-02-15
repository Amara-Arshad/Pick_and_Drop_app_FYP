package com.fyp.securepickanddrop.constantclasses;

import com.fyp.securepickanddrop.modelsclasses.Data;

public class Sender {
    public Data data;
    public String to;

    public Sender(Data data, String to) {
        this.data = data;
        this.to = to;
    }
}

