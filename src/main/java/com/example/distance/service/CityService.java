package com.example.distance.service;

import com.example.distance.entity.CityEntity;
import com.example.distance.entity.DistanceCityEntity;
import com.example.distance.entity.DistanceEntity;
import com.example.distance.repository.CityRepo;
import com.example.distance.repository.DistanceCityRepo;
import com.example.distance.repository.DistanceRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CityService {
    private final CityRepo cityRepo;
    private final DistanceRepo distanceRepo;
    private final DistanceCityRepo distanceCityRepo;
    public CityService(CityRepo cityRepo, DistanceRepo distanceRepo, DistanceCityRepo distanceCityRepo) {

        this.cityRepo = cityRepo;
        this.distanceRepo = distanceRepo;
        this.distanceCityRepo = distanceCityRepo;
    }

    public CityEntity saveCity(String name, double latitude, double longitude) {
        CityEntity existingCity = cityRepo.findByName(name);

        if (existingCity != null) {
            return existingCity;
        } else {
            CityEntity city = new CityEntity();
            city.setName(name);
            city.setLatitude(latitude);
            city.setLongitude(longitude);
            return cityRepo.save(city);
        }
    }

    public CityEntity createCity(CityEntity city) {
        return cityRepo.save(city);
    }

    public Optional<CityEntity> getCityById(Long id) {
        return cityRepo.findById(id);
    }

    public CityEntity updateCity(Long id, CityEntity updatedCity) {
        if (cityRepo.existsById(id)) {
            updatedCity.setId(id);
            return cityRepo.save(updatedCity);
        } else {
            return null;
        }
    }

    public boolean deleteCityAndRelatedDistances(Long id) {
        Optional<CityEntity> cityEntityOptional = cityRepo.findById(id);
        if (cityEntityOptional.isPresent()) {
            CityEntity city = cityEntityOptional.get();
            List<DistanceEntity> relatedDistances = distanceRepo.findByCity1OrCity2(city, city);
            distanceRepo.deleteAll(relatedDistances);
            cityRepo.delete(city);

            // Удаление связей с таблицей distance_city_entity
            for (DistanceEntity distance : relatedDistances) {
                List<DistanceCityEntity> distanceCityEntities = distance.getDistanceCityEntities();
                distanceCityRepo.deleteAll(distanceCityEntities);
            }

            return true;
        }
        return false;
    }

}
