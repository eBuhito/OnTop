package com.ontop.transfer.adapter.outbound.repository;

import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

@Service
public class KeyHolderFactoryImpl implements KeyHolderFactory {
    @Override
    public KeyHolder getGeneratedKeyHolder() {
        return new GeneratedKeyHolder();
    }
}
