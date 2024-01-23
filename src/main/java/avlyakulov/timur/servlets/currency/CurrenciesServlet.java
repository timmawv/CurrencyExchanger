package avlyakulov.timur.servlets.currency;

import avlyakulov.timur.custom_exception.CurrencyAlreadyExists;
import avlyakulov.timur.custom_exception.ErrorResponse;
import avlyakulov.timur.custom_exception.RequiredFormFieldIsMissing;
import avlyakulov.timur.dto.currency.CurrencyRequest;
import avlyakulov.timur.dto.currency.CurrencyResponse;
import avlyakulov.timur.mapper.CurrencyMapper;
import avlyakulov.timur.model.Currency;
import avlyakulov.timur.service.CurrencyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(urlPatterns = "/currencies")
@Slf4j
public class CurrenciesServlet extends HttpServlet {


    private final CurrencyService currencyService = new CurrencyService();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final CurrencyMapper currencyMapper = new CurrencyMapper();

    @Override
    public void init() throws ServletException {
        log.info("Currencies servlet was created");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("We are getting all currencies");
        List<CurrencyResponse> currencies = currencyService.findAll().stream().map(currencyMapper::mapToResponse).toList();
        PrintWriter out = resp.getWriter();
        resp.setStatus(HttpServletResponse.SC_OK);//status 200
        out.print(objectMapper.writeValueAsString(currencies));
        out.flush();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException  {
        String code = req.getParameter("code").toUpperCase();
        String fullName = req.getParameter("fullName");
        String sign = req.getParameter("sign");
        Currency currency = currencyMapper.mapToEntity(new CurrencyRequest(code, fullName, sign));
        PrintWriter out = resp.getWriter();
        log.info("We got a request to create currency with such parameters code {}, fullName {}, sign {}", code, fullName, sign);
        try {
            currency = currencyService.createCurrency(currency);
            log.info("Currency with such parameters was created");
            resp.setStatus(HttpServletResponse.SC_OK);//status 200
            out.print(objectMapper.writeValueAsString(currency));
        } catch (RequiredFormFieldIsMissing e) {
            log.error("One field of required fields is missing");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);//status 400
            out.print(objectMapper.writeValueAsString(new ErrorResponse(e.getMessage())));
        } catch (CurrencyAlreadyExists e) {
            log.error("The currency with such parameters is already exists");
            resp.setStatus(HttpServletResponse.SC_CONFLICT);//status 409
            out.print(objectMapper.writeValueAsString(new ErrorResponse(e.getMessage())));
        }
        out.flush();
    }

    @Override
    public void destroy() {
        log.info("Currencies servlet was destroyed");
    }
}