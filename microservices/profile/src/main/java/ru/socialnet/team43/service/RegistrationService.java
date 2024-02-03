package ru.socialnet.team43.service;

import ru.socialnet.team43.dto.RegDto;


public interface RegistrationService {

    boolean registrationPerson(RegDto regDto);
}
