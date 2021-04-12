package com.shahinnazarov.edd.krm.services.db;

import com.shahinnazarov.edd.krm.container.base.BaseService;
import com.shahinnazarov.edd.krm.container.dto.SendEmailDto;

public interface EmailMessageService extends BaseService {
    void save(SendEmailDto sendEmailDto);
}
