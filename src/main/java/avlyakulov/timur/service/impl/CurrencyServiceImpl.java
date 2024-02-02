package avlyakulov.timur.service.impl;

import avlyakulov.timur.custom_exception.BadCurrencyCodeException;
import avlyakulov.timur.custom_exception.CurrencyAlreadyExistsException;
import avlyakulov.timur.custom_exception.CurrencyNotFoundException;
import avlyakulov.timur.dao.CurrencyDao;
import avlyakulov.timur.model.Currency;
import avlyakulov.timur.service.CurrencyService;

import java.util.List;
import java.util.Optional;

public class CurrencyServiceImpl implements CurrencyService {
    private final int CODE_LENGTH_URL = 3;

    private final CurrencyDao currencyDao;

    public CurrencyServiceImpl(CurrencyDao currencyDao) {
        this.currencyDao = currencyDao;
    }


    public List<Currency> findAll() {
        return currencyDao.findAll();
    }

    public Currency findByCode(String code) {
        if (code.isBlank() || code.length() != CODE_LENGTH_URL) {
            throw new BadCurrencyCodeException("Currency code is missing at address or you put wrong code");
        } else {
            Optional<Currency> currency = currencyDao.findCurrencyByCode(code);
            if (currency.isEmpty()) {
                throw new CurrencyNotFoundException("Currency with this code " + code + " wasn't found");
            } else {
                return currency.get();
            }
        }
    }

    public Currency createCurrency(Currency currency) {
        Optional<Currency> currencyOptional = findSimpleByCode(currency.getCode());
        if (currencyOptional.isPresent()) {
            throw new CurrencyAlreadyExistsException("Currency with such code " + currency.getCode() + " is already exists");
        } else {
            return currencyDao.create(currency);
        }
    }

    private Optional<Currency> findSimpleByCode(String code) {
        return currencyDao.findCurrencyByCode(code);
    }
}