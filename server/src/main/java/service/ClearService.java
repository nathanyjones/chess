package service;

import dataaccess.DataAccess;
import service.result.ErrorResult;

public class ClearService {
    private final DataAccess dataAccess;

    public ClearService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public Object[] clear() {
        try {
            dataAccess.clear();
            return new Object[] {200, new Object()};
        } catch (Exception e) {
            return new Object[] {500, new ErrorResult("Error: " + e.getMessage())};
        }
    }

}
