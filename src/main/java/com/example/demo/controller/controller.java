/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.controller;

import com.example.demo.interfacesServices.IHappyDayService;
import com.example.demo.service.HolidaysResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Mateo
 */
@RestController
@RequestMapping("/happyDay")
public class controller {
    @Autowired
    private IHappyDayService service;
 
    @GetMapping("/paid/{date}")
    public HolidaysResponse listHoliday(@PathVariable String date) {
        return service.IsHappyDay(date);
    }
    
}
