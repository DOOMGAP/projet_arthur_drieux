package org.example.proxibanque.service;

import lombok.RequiredArgsConstructor;
import org.example.proxibanque.entity.Client;
import org.example.proxibanque.repository.ClientRepository;
import org.example.proxibanque.repository.ConseillerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepo;
    private final ConseillerRepository conseillerRepo;

    @Override
    public Client create(Client client, Long conseillerId) {
        var conseiller = conseillerRepo.findById(conseillerId)
                .orElseThrow(() -> new RuntimeException("Conseiller not found"));

        client.setConseiller(conseiller);
        return clientRepo.save(client);
    }

    @Override
    public List<Client> findByConseiller(Long conseillerId) {
        return clientRepo.findAll()
                .stream()
                .filter(c -> c.getConseiller().getId().equals(conseillerId))
                .toList();
    }
}

