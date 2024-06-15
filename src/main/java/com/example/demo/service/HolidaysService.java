package com.example.demo.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.interfaces.IHappyDay;
import com.example.demo.interfacesServices.IHappyDayService;
import com.example.demo.modelo.Holidays;
/**
 *
 * @author Mateo
 */
@Service
public class HolidaysService implements IHappyDayService {

    @Autowired
    private IHappyDay Data;

    private LocalDate obtenerDomingoPascua(int year) {
        int month, day, A, B, C, D, E, M, N;
        M = 0;
        N = 0;
        if (year >= 1583 && year <= 1699) {
            M = 22;
            N = 2;
        } else if (year >= 1700 && year <= 1799) {
            M = 23;
            N = 3;
        } else if (year >= 1800 && year <= 1899) {
            M = 23;
            N = 4;
        } else if (year >= 1900 && year <= 2099) {
            M = 24;
            N = 5;
        } else if (year >= 2100 && year <= 2199) {
            M = 24;
            N = 6;
        } else if (year >= 2200 && year <= 2299) {
            M = 25;
            N = 0;
        }

        A = year % 19;
        B = year % 4;
        C = year % 7;
        D = ((19 * A) + M) % 30;
        E = ((2 * B) + (4 * C) + (6 * D) + N) % 7;

        // Decidir entre los 2 casos
        if (D + E < 10) {
            day = D + E + 22;
            month = 3; // Marzo
        } else {
            day = D + E - 9;
            month = 4; // Abril
        }

        // Excepciones especiales
        if (day == 26 && month == 4) {
            day = 19;
        }
        if (day == 25 && month == 4 && D == 28 && E == 6 && A > 10) {
            day = 18;
        }

        return LocalDate.of(year, month, day);
    }

    private LocalDate agregarDias(LocalDate fecha, int dias) {
        return fecha.plusDays(dias);
    }

    private LocalDate siguienteLunes(LocalDate fecha) {
        int dayOfWeek = fecha.getDayOfWeek().getValue();
        if (dayOfWeek > 1) {
            return fecha.plusDays(8 - dayOfWeek);
        } else if (dayOfWeek < 1) {
            return fecha.plusDays(1);
        }
        return fecha;
    }

    private List<Holidays> calcularFestivos(List<Holidays> festivos, int year) {
        if (festivos != null) {
            LocalDate pascua = obtenerDomingoPascua(year);
            int i = 0;
            for (final Holidays festivo : festivos) {
                switch (festivo.getIdtipo()) {
                    case 1:
                        festivo.setFecha(convertToDate(LocalDate.of(year, festivo.getMes(), festivo.getDia())));
                        break;
                    case 2:
                        festivo.setFecha(convertToDate(siguienteLunes(LocalDate.of(year, festivo.getMes(), festivo.getDia()))));
                        break;
                    case 3:
                        festivo.setFecha(convertToDate(agregarDias(pascua, festivo.getDiaspascua())));
                        break;
                    case 4:
                        festivo.setFecha(convertToDate(siguienteLunes(agregarDias(pascua, festivo.getDiaspascua()))));
                        break;
                }
                festivos.set(i, festivo);
                i++;
            }
        }
        return festivos;
    }

    public List<LocalDate> obtenerFestivos(int year) {
        List<Holidays> festivos = Data.findAll();
        festivos = calcularFestivos(festivos, year);
        List<LocalDate> fechas = new ArrayList<>();
        for (final Holidays festivo : festivos) {
            fechas.add(convertToLocalDate(festivo.getFecha()));
        }
        return fechas;
    }

    private boolean fechasIguales(LocalDate fecha1, LocalDate fecha2) {
        return fecha1.equals(fecha2);
    }

    private boolean esFestivo(List<Holidays> festivos, LocalDate fecha) {
        if (festivos != null) {
            festivos = calcularFestivos(festivos, fecha.getYear());
            for (final Holidays festivo : festivos) {
                if (fechasIguales(convertToLocalDate(festivo.getFecha()), fecha)) {
                    return true;
                }
            }
        }
        return false;
    }

    private Date convertToDate(LocalDate localDate) {
        ZonedDateTime zonedDateTime = localDate.atStartOfDay(ZoneId.systemDefault());
        Instant instant = zonedDateTime.toInstant();
        return Date.from(instant);
    }

    private LocalDate convertToLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public Integer esDiaValido(LocalDate date, List<Holidays> festivos) {
        Boolean status = esFestivo(festivos, date);
        int dayOfWeek = date.getDayOfWeek().getValue();
        if (dayOfWeek == 7) {
            return 2;
        }
        if (dayOfWeek == 6 || status) {
            return 1;
        }
        return 10;
    }
    
    public LocalDate PayDay(LocalDate date){
        // la nómina se paga los días 15 y 30 de cada mes
        if(date.getDayOfMonth() <= 15) {
            return LocalDate.of(date.getYear(), date.getMonthValue(), 15);
        }
        return LocalDate.of(date.getYear(), date.getMonthValue(), 30);
    }

    @Override
    public HolidaysResponse IsHappyDay(String date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        formatter.setLenient(false);

        try {
            Date dateformatter = formatter.parse(date);
            List<Holidays> festivos = Data.findAll();
            LocalDate dateAux = PayDay(convertToLocalDate(dateformatter));
            while (esDiaValido(dateAux, festivos) != 10) {
                dateAux = dateAux.minusDays(esDiaValido(dateAux, festivos));
            }
            return new HolidaysResponse(dateAux.toString());
        } catch (ParseException e) {
            e.printStackTrace();
            return new HolidaysResponse("error");
        }
    }
}
