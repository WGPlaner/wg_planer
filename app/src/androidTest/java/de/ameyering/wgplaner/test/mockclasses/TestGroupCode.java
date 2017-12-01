package de.ameyering.wgplaner.test.mockclasses;

import io.swagger.client.model.GroupCode;


public class TestGroupCode extends GroupCode {

    @Override
    public String getCode() {
        return "1SUPERCODE";
    }
}
