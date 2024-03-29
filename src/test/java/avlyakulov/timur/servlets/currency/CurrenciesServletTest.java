package avlyakulov.timur.servlets.currency;

import avlyakulov.timur.custom_exception.RequiredFormFieldIsMissingException;
import avlyakulov.timur.model.Currency;
import avlyakulov.timur.service.CurrencyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurrenciesServletTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private CurrencyService currencyService;

    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private CurrenciesServlet currenciesServlet;


    @Test
    void createCurrency_CurrencyWasCreated_CurrencyIsValid() throws IOException, ServletException {
        // Задаем параметры запроса
        Currency expectedCurrency = new Currency(Long.valueOf("1"), "USD", "US Dollar", "$");
        when(request.getParameter("code")).thenReturn("USD");
        when(request.getParameter("name")).thenReturn("US Dollar");
        when(request.getParameter("sign")).thenReturn("$");
        when(currencyService.createCurrency(any())).thenReturn(expectedCurrency);
        when(response.getWriter()).thenReturn(new PrintWriter(new StringWriter()));
        when(objectMapper.writeValueAsString(expectedCurrency)).thenReturn(any());

        currenciesServlet.doPost(request, response);

        verify(currencyService, times(1)).createCurrency(any());
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void createCurrency_CurrencyNotCreated_RequiredParameterIsMissing() throws IOException, ServletException {
        when(request.getParameter("code")).thenReturn(null);
        when(request.getParameter("name")).thenReturn("US Dollar");
        when(request.getParameter("sign")).thenReturn("$");

        Assertions.assertThrows(RequiredFormFieldIsMissingException.class, () -> currenciesServlet.doPost(request, response));
    }


//    MethodName_ExpectedBehavior_StateUnderTest
//
//    cons: should be renamed if method change name
//
//    example: isAdult_False_AgeLessThan18


}