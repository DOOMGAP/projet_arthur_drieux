package org.example.proxibanque.service;

import org.example.proxibanque.entity.Client;

import java.util.List;

public interface ClientService {
    Client create(Client client, Long conseillerId);
    List<Client> findByConseiller(Long conseillerId);
}
