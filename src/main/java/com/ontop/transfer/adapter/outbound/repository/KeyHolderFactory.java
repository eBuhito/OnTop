package com.ontop.transfer.adapter.outbound.repository;

import org.springframework.jdbc.support.KeyHolder;

public interface KeyHolderFactory {
    KeyHolder getGeneratedKeyHolder();
}
