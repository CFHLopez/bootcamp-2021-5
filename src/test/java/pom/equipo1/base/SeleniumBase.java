package pom.equipo1.base;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SeleniumBase {

    //Atributos
    WebDriver driver;
    private int segundosEspera = 10;

    //Constructor para traernos driver
    public SeleniumBase(WebDriver driver){
        this.driver = driver;
    }

    //Metodos Wrapper - Envoltorio
    public WebElement encontrarElemento(By localizador){
        return driver.findElement(localizador);
    }

    public void irUrl(String URL){
        driver.get(URL);
    }

    public void darClick(By localizador){
        // esperaExplicitaElementoClickeable(localizador);
        encontrarElemento(localizador).click();
    }

    public void esperaImplicita(){
        driver.manage().timeouts().implicitlyWait(segundosEspera, TimeUnit.SECONDS);
    }

    public void esperaExplicitaElementoClickeable(By localizador){
        WebDriverWait espera;
        espera = new WebDriverWait(driver,segundosEspera);
        espera.until(ExpectedConditions.elementToBeClickable(localizador));
    }

    public void esperaExplicitaElementoVisible(By localizador){
        WebDriverWait espera;
        espera = new WebDriverWait(driver,segundosEspera);
        espera.until(ExpectedConditions.visibilityOfElementLocated(localizador));
    }

    public String obtenerTexto(By localizador){
        return encontrarElemento(localizador).getText();
    }

    public List<WebElement> generarLista(By localizador){
        List<WebElement> lista = driver.findElements(localizador);
        return lista;
    }

    public void busquedaElemento (List<WebElement> lista,String palabra){
        for (WebElement l: lista){
            // RECORREMOS LA LISTA HASTA ENCONTRAR LA PALABRA REQUERIDA
            if (l.getText().contains(palabra)){
                // HACEMOS CLICK EN LA CATEGORIA SELECCIONADA
                l.click();
                break;
            }
        }
    }

    public void ingresarDestino (String textoIngresado, String seleccionar, By localizador, By espera){
        esperaExplicitaElementoVisible(localizador);
        driver.findElement(localizador).sendKeys(textoIngresado);
        esperaExplicitaElementoVisible(espera);
        List<WebElement> resultados = generarLista(espera);
        busquedaElemento(resultados,seleccionar);
    }

    public void buscarEnCalendario (int fecha1, int fecha2, By localizador,By localizador2){
        // LISTA CALENTARIO ACTUAL
        esperaExplicitaElementoVisible(localizador);
        List<WebElement> mesActual = generarLista(localizador);
        // INGRESAR FECHAS
        // TOMAMOS EL DIA ACTUAL
        Calendar c1 = Calendar.getInstance();
        String dia = Integer.toString(c1.get(Calendar.DATE));
        int diaActual = Integer.parseInt(dia);
        // ULTIMO DIA DEL CALENDARIO ACTUAL
        String diaMax = mesActual.get(mesActual.size()-1).getText();
        int diaUltimo = Integer.parseInt(diaMax);
        // LA FECHA ACTUAL + 1 DIA PASA AL SIGUIENTE MES?
        if (diaActual+fecha2 < diaUltimo){
            buscarCalendario(diaActual+fecha1,mesActual);
            buscarCalendario(diaActual+fecha2,mesActual);
        }
        else{
            if (diaActual+fecha1 < diaUltimo){
                buscarCalendario(diaActual+fecha1,mesActual);
                driver.findElement(localizador2).click();
                mesActual= generarLista(By.cssSelector("._dpmg2--show ._dpmg2--month-active ._dpmg2--available span._dpmg2--date-number"));
                buscarCalendario(diaActual+fecha2-diaUltimo,mesActual);
            }
            else {
                darClick(localizador2);
                mesActual= generarLista(By.cssSelector("._dpmg2--show ._dpmg2--month-active ._dpmg2--available span._dpmg2--date-number"));
                buscarCalendario(diaActual+fecha1-diaUltimo,mesActual);
                buscarCalendario(diaActual+fecha2-diaUltimo,mesActual);
            }
        }
    }

    public void ingresarUnaFecha(int dia,By localizador){
        Calendar c1 = Calendar.getInstance();
        String diaAct = Integer.toString(c1.get(Calendar.DATE));
        int diaActual = Integer.parseInt(diaAct);
        esperaExplicitaElementoVisible(By.cssSelector("._dpmg2--show ._dpmg2--month-active span"));
        // MES TOMAMOS EL CALENDARIO DEL MES DE ENERO (HASTA)
        List<WebElement> mesActual = generarLista(localizador);
        String diaMax = mesActual.get(mesActual.size()-1).getText();
        int diaUltimo = Integer.parseInt(diaMax);
        if (diaActual+dia < diaUltimo){
            // BUSCAMOS Y SELECCIONAMOS LA FECHA
            buscarCalendario(diaActual+dia,mesActual);
        }
        else{
            darClick(By.xpath("//i[@class='_dpmg2--icon-ico-arrow']//ancestor::div[@class='datepicker-packages-car sbox-v4-components']"));
            mesActual = generarLista(localizador);
            buscarCalendario(diaActual+dia-diaUltimo,mesActual);
        }
    }

    private void buscarCalendario(int diaBuscado, List<WebElement> mes){
        String dia = Integer.toString(diaBuscado);
        busquedaElemento(mes,dia);
    }

    public void fechasLejanas(String mes,int fecha1,int fecha2, By localizador){
        esperaExplicitaElementoVisible(By.cssSelector("._dpmg2--show ._dpmg2--month-active span"));
        buscarMes(mes);
        List<WebElement> mesBuscado = generarLista(localizador);
        buscarCalendario(fecha1,mesBuscado);
        buscarCalendario(fecha2,mesBuscado);
    }

    public void ingresarUnaFechaEspecifica(int dia,By localizador){
        esperaExplicitaElementoVisible(By.cssSelector("._dpmg2--show ._dpmg2--month-active span"));
        // MES TOMAMOS EL CALENDARIO DEL MES DE ENERO (HASTA)
        List<WebElement> mes = generarLista(localizador);
        // BUSCAMOS Y SELECCIONAMOS LA FECHA
        buscarCalendario(dia,mes);
    }

    private void buscarMes (String mes){
        String mesActual = obtenerTexto(By.cssSelector("._dpmg2--show ._dpmg2--month-active span"));
        while (!mesActual.contains(mes)){
            // AVANZAMOS AL SIGUIENTE MES
            darClick(By.cssSelector("body > div.datepicker-packages.sbox-v4-components > div > div._dpmg2--controlsWrapper > div._dpmg2--controls-next > i"));
            esperaExplicitaElementoVisible(By.cssSelector("._dpmg2--show ._dpmg2--month-active span"));
            // TOMAMOS EL NOMBRE DEL MES SIGUIENTE
            mesActual = obtenerTexto(By.cssSelector("._dpmg2--show ._dpmg2--month-active span"));
        }
    }

    public void cantidadPasajeros (int esperado, By localizador){
        String num =driver.findElement(localizador).getAttribute("value");
        int valorActual = Integer.parseInt(num);
        adultos(valorActual,esperado);
    }

    public void cantidadOcupantesHabitacion (int esperado, By localizador){
        String num =driver.findElement(localizador).getAttribute("value");
        int valorActual = Integer.parseInt(num);
        ocupantesHabitacion(valorActual,esperado);
    }

    private void adultos (int real, int esperado){
        while (esperado != real){
            if (real > esperado){
                darClick(By.cssSelector("div.distpicker.distpicker-rooms-packages.sbox-v4-components a.steppers-icon-left.sbox-3-icon-minus"));
                real--;
            }
            else{
                darClick(By.cssSelector("div.distpicker.distpicker-rooms-packages.sbox-v4-components a.steppers-icon-right.sbox-3-icon-plus"));
                real++;
            }
        }
    }

    private void ocupantesHabitacion (int real, int esperado){
        while (esperado != real){
            if (real > esperado){
                darClick(By.cssSelector("div._pnlpk-itemRow__item a.steppers-icon-left.sbox-3-icon-minus"));
                real--;
            }
            else{
                darClick(By.cssSelector("div._pnlpk-itemRow__item a.steppers-icon-right.sbox-3-icon-plus"));
                real++;
            }
        }
    }

    public int contarElementos (By localizador){
        esperaExplicitaElementoVisible(localizador);
        int cantidad = driver.findElements(localizador).size();
        return cantidad;
    }

    public String validarDatos (String lugar, By localizador){
        WebElement dato=driver.findElement(localizador);
        String mensaje = dato.getText();
        Pattern patron = Pattern.compile(lugar);
        Matcher m = patron.matcher(mensaje);
        boolean e = m.find();
        String res = String.valueOf(e);
        return res;
    }

    public void edadMenor (int edad){
        /// SUMAMOS 1 NIÑO
        driver.findElement(By.cssSelector("div.distpicker.distpicker-rooms-packages.sbox-v4-components div._pnlpk-itemRow__item._pnlpk-stepper-minors.-medium-down-to-lg a.steppers-icon-right.sbox-3-icon-plus")).click();
        // CLICK EN EDAD
        driver.findElement(By.cssSelector("div.distpicker.distpicker-rooms-packages.sbox-v4-components div._pnlpk-itemRow__item._pnlpk-select-minor-age select")).click();
        List <WebElement> edades = generarLista(By.cssSelector("div.distpicker.distpicker-rooms-packages.sbox-v4-components select option"));
        String dia = Integer.toString(edad);
        busquedaElemento(edades,dia);
    }

    public void seleccionbox (String clase, String palabra) throws InterruptedException {
        esperaExplicitaElementoClickeable(By.cssSelector(clase));
        driver.findElement(By.cssSelector(clase)).click();
        esperaExplicitaElementoVisible(By.cssSelector(clase+" select option"));
        List <WebElement> options = driver.findElements(By.cssSelector(clase+" select option"));
        // CAMBIAMOS A DÓLARES ESTADOUNIDENSES
        busquedaElemento(options,palabra);
        esperaExplicitaElementoVisible(By.cssSelector(clase+" select option"));
        // <div class="loader-modal-content">
        Thread.sleep(2000);
    }

    public void filtrar (String filtro,  By localizador) {
        WebDriverWait espera = new WebDriverWait(driver, 15);
        List<WebElement> optionsTO = driver.findElements(localizador);
        espera.until(ExpectedConditions.elementToBeClickable(localizador));
        busquedaElemento(optionsTO,filtro);
    }

    public void buscarPorFiltro(String labelAFiltrar,String opcionAClickear){
        esperaExplicitaElementoVisible(By.cssSelector("div.eva-3-filters-vertical.-eva-3-mb-lg.-show-filters"));
        List<WebElement> optionsTO = driver.findElements(By.xpath("//li[contains(.,'"+labelAFiltrar+"')]//em"));
        busquedaElemento(optionsTO,opcionAClickear);
        esperaExplicitaElementoVisible(By.cssSelector("div.eva-3-filters-vertical.-eva-3-mb-lg.-show-filters"));
        esperaImplicita();
    }

   public void cambiarPestaña(){
        String mainTab = driver.getWindowHandle();
        String newTab = "";
        Set<String> handles = driver.getWindowHandles();
        for (String actual : handles) {
            if (!actual.equalsIgnoreCase(mainTab)) {
                driver.switchTo().window(actual);
                newTab = actual;
            }
        }
        esperaImplicita();
    }

    public void cerrarPestaña(){
        driver.quit();
    }

    public void cambiarPestañaInicial(){
        String mainTab = driver.getWindowHandle();
        driver.switchTo().window(mainTab);
    }

    public void cambiarPesos(String palabra, By localizador) {
        List<WebElement> lista = driver.findElements(localizador);
        busquedaElemento(lista,palabra);
    }
    public void clickBotoAplicar(By localizador) {
        esperaExplicitaElementoClickeable(localizador);
        encontrarElemento(localizador).click();
    }

    public void cantpasajerosTraslado(int FechaEsperada, By localizador){
        String num =driver.findElement(localizador).getAttribute("value");
        int numero = Integer.parseInt(num);
        while (numero != 4){
            if (numero > 4){
                driver.findElement(By.cssSelector("div._pnlpk-itemRow__item a.steppers-icon.left")).click();
                numero--;
            }
            else{
                driver.findElement(By.cssSelector("div._pnlpk-itemRow__item a.steppers-icon-right.sbox-3-icon-plus")).click();
                numero++;
            }
        }
        adultos(numero,FechaEsperada);
    }

    public void seleccionarEnComprar(String compra, By localizador){
        WebDriverWait espera = new WebDriverWait(driver, 15);
        List<WebElement> btn= driver.findElements(localizador);
        espera.until(ExpectedConditions.elementToBeClickable(localizador));
        busquedaElemento(btn,compra);
    }
}
