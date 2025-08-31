package com.uade.tpo.pixelpoint.controllers;

import java.util.ArrayList;

// Spring Imports
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uade.tpo.pixelpoint.entity.catalog.DeviceModel;
import com.uade.tpo.pixelpoint.services.DeviceModelService;

@RestController
@RequestMapping("deviceModel")
public class DeviceModelController {
    // Obtener todas las marcas
    @GetMapping()
    public ArrayList<DeviceModel> getDeviceModel() {
		DeviceModelService deviceModelService = new DeviceModelService();
        return deviceModelService.getDevice();
    }
    
    // Obtener por id
	@GetMapping("{deviceId}") // GET - localhost:****/brand/3
	public DeviceModel getDeviceMoedlById(@PathVariable Long deviceId) {
		DeviceModelService deviceModelService = new DeviceModelService();
        return deviceModelService.getDeviceModelById(deviceId);
	}

    // Crear nueva Brand
	@PostMapping
	public DeviceModel postDeviceModel(@RequestBody String deviceId) {
		DeviceModelService deviceModelService = new DeviceModelService();
        return deviceModelService.createDeviceModel(deviceId);
	}

}
