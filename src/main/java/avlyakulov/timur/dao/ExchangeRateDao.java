package avlyakulov.timur.dao;

import avlyakulov.timur.connection.ConnectionBuilder;
import avlyakulov.timur.model.ExchangeRate;

public interface ExchangeRateDao extends CrudDao<ExchangeRate, Integer> {

    ExchangeRate findByCodes(String baseCurrencyCode, String targetCurrencyCode);

    void setConnectionBuilder(ConnectionBuilder connectionBuilder);
}