package kr.keumyoung.mukin.api;

/**
 *  on 02/09/17.
 */

public class RequestModel<T> {
    T resource;

    public RequestModel(T resource) {
        this.resource = resource;
    }
}
