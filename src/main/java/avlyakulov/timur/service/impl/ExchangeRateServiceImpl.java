package avlyakulov.timur.service.impl;

import avlyakulov.timur.custom_exception.CurrencyNotFoundException;
import avlyakulov.timur.custom_exception.ExchangeRateAlreadyExistsException;
import avlyakulov.timur.custom_exception.ExchangeRateCurrencyCodePairException;
import avlyakulov.timur.custom_exception.ExchangeRateCurrencyPairNotFoundException;
import avlyakulov.timur.dao.CurrencyDao;
import avlyakulov.timur.dao.ExchangeRateDao;
import avlyakulov.timur.model.Currency;
import avlyakulov.timur.model.ExchangeRate;
import avlyakulov.timur.service.ExchangeRateService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


public class ExchangeRateServiceImpl implements ExchangeRateService {
    private final int CURRENCY_PAIR_CODE_LENGTH_URL = 6;

    private final ExchangeRateDao exchangeRateDao;
    private final CurrencyDao currencyDao;

    public ExchangeRateServiceImpl(ExchangeRateDao exchangeRateDao, CurrencyDao currencyDao) {
        this.exchangeRateDao = exchangeRateDao;
        this.currencyDao = currencyDao;
    }


    public List<ExchangeRate> findAll() {
        return exchangeRateDao.findAll();
    }

    public ExchangeRate findByCodes(String currencyPairCode) {
        if (currencyPairCode.isBlank() || currencyPairCode.length() != CURRENCY_PAIR_CODE_LENGTH_URL) {
            throw new ExchangeRateCurrencyCodePairException("The currency codes of the pair are missing from the address or it is specified incorrectly");
        } else {
            String baseCurrencyCode = currencyPairCode.substring(0, 3);
            String targetCurrencyCode = currencyPairCode.substring(3);

            Optional<ExchangeRate> exchangeRate = exchangeRateDao.findByCodes(baseCurrencyCode, targetCurrencyCode);
            if (exchangeRate.isPresent()) {
                return exchangeRate.get();
            } else {
                throw new ExchangeRateCurrencyPairNotFoundException("The exchange rate with such code pair " + currencyPairCode + " doesn't exist");
            }
        }
    }

    public ExchangeRate createExchangeRate(ExchangeRate exchangeRate) {
        String baseCurrencyCode = exchangeRate.getBaseCurrency().getCode();
        String targetCurrencyCode = exchangeRate.getTargetCurrency().getCode();
        Optional<ExchangeRate> exchangeRateByCodes = exchangeRateDao.findByCodes(baseCurrencyCode, targetCurrencyCode);

        if (exchangeRateByCodes.isEmpty()) {
            Optional<Currency> baseCurrency = currencyDao.findCurrencyByCode(baseCurrencyCode);
            Optional<Currency> targetCurrency = currencyDao.findCurrencyByCode(targetCurrencyCode);
            if (baseCurrency.isPresent() && targetCurrency.isPresent()) {
                return exchangeRateDao.create(exchangeRate);
            } else {
                throw new CurrencyNotFoundException("One (or both) currencies from the currency pair does not exist in the database");
            }
        } else {
            throw new ExchangeRateAlreadyExistsException("A currency pair with this code pair already exists ");
        }
    }


    public ExchangeRate updateExchangeRate(String currencyPairCode, BigDecimal updatedRate) {
        if (currencyPairCode.isBlank() || currencyPairCode.length() != CURRENCY_PAIR_CODE_LENGTH_URL) {
            throw new ExchangeRateCurrencyCodePairException("The currency codes of the pair are missing from the address or it is specified incorrectly");
        } else {
            String baseCurrencyCode = currencyPairCode.substring(0, 3);
            String targetCurrencyCode = currencyPairCode.substring(3);

            Optional<ExchangeRate> exchangeRate = exchangeRateDao.findByCodes(baseCurrencyCode, targetCurrencyCode);
            if (exchangeRate.isPresent()) {
                return exchangeRateDao.update(baseCurrencyCode, targetCurrencyCode, updatedRate);
            } else {
                throw new ExchangeRateCurrencyPairNotFoundException("The exchange rate with such code pair " + currencyPairCode + " doesn't exist");
            }
        }
    }

}