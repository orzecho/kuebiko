# Opinion mining w kontekście tekstów publicystycznych w internecie

## Stan na 30 lipca 2018 

### Crawler
* crawler chodzi po stronie https://www.theguardian.com/
* wyciąga treść artykułu z tagów html
* wyciąga tagi artykułu z tagów html
* wyciąga datę z adresu 
* odfiltrowuje mało interesujące artykuły (np. galerie)
* zapisuje zebrane artykuły w raz z meta informacjami w bazie danych
* po zakończeniu przeszukiwania czeka 24 godziny i uruchamia się ponownie

### Word2Vec
* po zebraniu odpowiednio dużej ilości artykułów zaczyna trenować model
* po zakończeniu treningu czeka 24 godziny i zaczyna ponownie, dotrenowując istniejący już model ale z nowymi, nieprzeprocesowanymi jeszcze artykułami
* model zapisuje do pliku
