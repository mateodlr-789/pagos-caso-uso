/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.interfaces;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.modelo.Holidays;

/**
 *
 * @author Mateo
 */
@Repository
public interface IHappyDay extends JpaRepository<Holidays, Integer>  {
    
}
