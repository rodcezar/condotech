-- TABLES --

CREATE TABLE ENDERECO (
    ID_ENDERECO             INTEGER  NOT NULL,
    LOGRADOURO              CHAR(60) NOT NULL,
    CEP                     CHAR(09) NULL,
    NUMERO                  SMALLINT NOT NULL,
    
    PRIMARY KEY (ID_ENDERECO)
);

CREATE TABLE CLIENTE (
       CPF                  CHAR(14) NOT NULL,
       NOME                 CHAR(30) NOT NULL,
       ID_ENDERECO          SMALLINT NOT NULL,
       TELEFONE             CHAR(20) NOT NULL,
       DATA_NASCIMENTO      CHAR(20) NOT NULL,
       
       PRIMARY KEY (CPF), 
       FOREIGN KEY (ID_ENDERECO) REFERENCES ENDERECO
);

CREATE TABLE CONDOMINIO (
       ID_CONDOMINIO        SMALLINT NOT NULL,
       ID_ENDERECO          SMALLINT NOT NULL,
       NOME                 CHAR(30) NOT NULL,
       CPF_RESPONSAVEL      CHAR(14) NOT NULL,

       PRIMARY KEY (ID_CONDOMINIO),
       FOREIGN KEY (ID_ENDERECO) REFERENCES ENDERECO,
       FOREIGN KEY (CPF_RESPONSAVEL)   REFERENCES CLIENTE(CPF)
);

CREATE TABLE PREDIO (
       ID_PREDIO            SMALLINT NOT NULL,
       ID_CONDOMINIO        SMALLINT NOT NULL,
       NOME                 CHAR(30) NOT NULL,
       CPF_SINDICO          CHAR(14) NOT NULL,
       NUMERO               SMALLINT NOT NULL,
       ID_ENDERECO          SMALLINT NOT NULL,           
       
       PRIMARY KEY (ID_PREDIO),
       FOREIGN KEY (ID_ENDERECO)        REFERENCES ENDERECO,
       FOREIGN KEY (ID_CONDOMINIO)      REFERENCES CONDOMINIO,
       FOREIGN KEY (CPF_SINDICO)        REFERENCES CLIENTE (CPF)
);

CREATE TABLE APARTAMENTO (
       ID_PREDIO            SMALLINT NOT NULL,
       NUMERO               SMALLINT NOT NULL,
       
       PRIMARY KEY (ID_PREDIO, NUMERO),
       FOREIGN KEY (ID_PREDIO)           REFERENCES PREDIO
);


CREATE TABLE COTA (
       CPF                  CHAR(14) NOT NULL,
       ID_PREDIO            SMALLINT NOT NULL,
       NUMERO               SMALLINT NOT NULL,
       QUANTIDADE           SMALLINT NOT NULL,
       
       FOREIGN KEY (CPF)                  REFERENCES CLIENTE,
       FOREIGN KEY (ID_PREDIO, NUMERO)    REFERENCES APARTAMENTO
);

-- STORED PROCEDURES --

CREATE OR REPLACE FUNCTION get_proprietario(id_predio int, numero int) RETURNS TEXT AS $$
DECLARE
	PROPRIETARIO TEXT;
	MAIOR_COTA INT;
	COTA_CLIENTE INT;	
	
BEGIN
	SELECT MAX(QUANTIDADE) INTO MAIOR_COTA
	FROM COTA A 
	WHERE A.ID_PREDIO = $1  
	  AND A.NUMERO = $2;

	SELECT B.NOME, A.QUANTIDADE INTO PROPRIETARIO, COTA_CLIENTE
	FROM COTA A, CLIENTE B
	WHERE A.ID_PREDIO = $1 
	  AND A.NUMERO = $2
	  AND A.QUANTIDADE = MAIOR_COTA
	  AND A.CPF = B.CPF
	  LIMIT 1;
	  
	IF PROPRIETARIO IS NULL OR PROPRIETARIO = '' 
	THEN
	    RETURN 'À VENDA';
	ELSIF COTA_CLIENTE < 100 THEN
        RETURN PROPRIETARIO || ' (COTISTA PRINCIPAL %' || COTA_CLIENTE::TEXT || ')';
	END IF;
		
	RETURN PROPRIETARIO;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION insere_cota(pCpf TEXT, pId_predio int, pNumero int, pQuantidade int) RETURNS TEXT AS $$
DECLARE
	SOMA_COTAS INT;
	COTAS_RESTANTES INT;
	MYCPF TEXT;
BEGIN

	IF $4 > 100 THEN 
		RETURN 'NÃO É POSSÍVEL VENDER MAIS DE 100 COTAS';
	END IF;

	SELECT SUM(C.QUANTIDADE) INTO SOMA_COTAS 
	FROM COTA C
	WHERE C.ID_PREDIO = $2
	  AND C.NUMERO = $3;

        IF SOMA_COTAS = 100 THEN
		RETURN 'APARTAMENTO JÁ VENDIDO';
	ELSE
		IF $4 > (100 - SOMA_COTAS) THEN
			COTAS_RESTANTES = 100 - SOMA_COTAS;
			RETURN 'SÓ RESTAM ' || COTAS_RESTANTES::TEXT || ' COTAS DISPONÍVEIS!';
		ELSE
			SELECT C.CPF FROM COTA C INTO MYCPF
			WHERE C.ID_PREDIO = $2
			  AND C.NUMERO = $3
			  AND C.CPF = $1;

			IF NOT FOUND THEN
				INSERT INTO COTA
				VALUES($1, $2, $3, $4);
				RETURN 'VENDA REALIZADA';
			ELSE
				UPDATE COTA
				SET QUANTIDADE = QUANTIDADE + $4
				WHERE CPF  = $1
				  AND ID_PREDIO = $2
				  AND NUMERO = $3;
				RETURN 'VENDA REALIZADA';
			END IF;
		END IF;
	END IF;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION update_sindico(p_id_predio int, p_cpf TEXT) RETURNS TEXT AS $$
DECLARE
	CURRENT_SINDICO TEXT;
	NUM_APTOS INT;
BEGIN
	SELECT C.CPF INTO CURRENT_SINDICO
	FROM CLIENTE C
	WHERE C.CPF = $2;
	IF NOT FOUND 
		THEN RETURN 'CLIENTE NÃO EXISTE';
	END IF;
	
	SELECT COUNT(*) INTO NUM_APTOS
	FROM COTA A 
	WHERE A.ID_PREDIO = $1  
	  AND A.CPF = $2
	  AND A.QUANTIDADE >= 100;

	IF NUM_APTOS > 0 THEN
		SELECT P.CPF_SINDICO INTO CURRENT_SINDICO
		FROM PREDIO P
		WHERE P.CPF_SINDICO = $2
		  AND P.ID_PREDIO = $1;

		IF NOT FOUND THEN
			UPDATE PREDIO
			SET CPF_SINDICO = $2
			WHERE ID_PREDIO = $1;
			RETURN 'SINDICO ATUALIZADO!';
		ELSE
			RETURN 'MESMO SÍNDICO';
		END IF;
	ELSE
		RETURN 'O CLIENTE NÃO PODE SER SÍNDICO';
	END IF;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION sindico_audit() RETURNS TRIGGER AS $cota$
DECLARE
	CURRENT_SINDICO TEXT;
BEGIN
	IF (TG_OP = 'UPDATE') THEN
		IF OLD.QUANTIDADE = 100 AND NEW.QUANTIDADE < 100 THEN

			SELECT CPF INTO CURRENT_SINDICO
			FROM COTA
			WHERE QUANTIDADE = 100;

			IF NOT FOUND THEN

				UPDATE PREDIO
				SET CPF_SINDICO = '000.000.000-00' 
				WHERE ID_PREDIO = OLD.ID_PREDIO;
				
			END IF;

		END IF;
	END IF;
			
	IF (TG_OP = 'DELETE') THEN

		SELECT CPF INTO CURRENT_SINDICO
		FROM COTA
		WHERE QUANTIDADE = 100;

		IF NOT FOUND THEN

			UPDATE PREDIO
			SET CPF_SINDICO = '000.000.000-00'  
			WHERE ID_PREDIO = OLD.ID_PREDIO;

		END IF;

	END IF;

	RETURN NEW;

END;
$cota$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION check_cpf(p_cpf TEXT) RETURNS BOOLEAN AS $$
DECLARE
	WS_CPF TEXT;
BEGIN
	SELECT C.CPF INTO WS_CPF
	FROM CLIENTE C 
	WHERE C.CPF = $1;

	IF FOUND THEN
		RETURN TRUE;
	ELSE
		RETURN FALSE;
	END IF;
	
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION delete_predio(p_id_predio INT) RETURNS BOOLEAN AS $$
DECLARE
	AFFECTEDROWS INT;
BEGIN
	DELETE FROM COTA WHERE id_predio = $1;

	DELETE FROM APARTAMENTO WHERE id_predio = $1; 
	
	WITH C AS (DELETE FROM PREDIO WHERE id_predio = $1 RETURNING 1)
	SELECT COUNT(*) INTO AFFECTEDROWS FROM C;
	IF AFFECTEDROWS = 1 THEN
		RETURN TRUE;
	ELSE
		RETURN FALSE;
	END IF;
	
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION get_percentual_vendido(id_condominio int) RETURNS INT AS $$
DECLARE
	TOTAL_CONDOMINIO INT;
	TOTAL_VENDIDO INT;	
BEGIN

	SELECT SUM(A.QUANTIDADE) INTO TOTAL_VENDIDO
	FROM COTA A, PREDIO B 
	WHERE A.ID_PREDIO = B.ID_PREDIO 
	  AND B.ID_CONDOMINIO = $1;

	SELECT COUNT(*)*100 TOTAL_VENDIDO INTO TOTAL_CONDOMINIO
	FROM APARTAMENTO A, PREDIO B
	WHERE A.ID_PREDIO = B.ID_PREDIO 
	  AND B.ID_CONDOMINIO = $1;

	RETURN (TOTAL_VENDIDO*100)/TOTAL_CONDOMINIO;
	  
  
END;
$$ LANGUAGE plpgsql;



-- TRIGGERS -- 

CREATE TRIGGER cota_update
    AFTER UPDATE OR DELETE ON cota
    FOR EACH ROW EXECUTE PROCEDURE sindico_audit();

-- INSERT STUFF

insert into endereco
values(1, 'Av. Pasteur, Urca, Rio de Janeiro - RJ', '22290-255', 458);

insert into CLIENTE
values('999.888.777-66', 'Tanaka', 1, '(21)3847-4738', '<unknown>');

insert into CLIENTE
values('000.000.000-00', 'Administradora', 1, '(21)9999-9999', '<unknown>');

insert into CLIENTE
values('123.456.789-11', 'Luke Skywalker', 1, '(21)9876-1234', '01/01/1900');

insert into CLIENTE
values('222.222.222-22', 'Rodrigo', 1, '(21)7777-8888', '01/01/1900');

insert into CONDOMINIO 
values(1, 1, 'UNIRIO', '999.888.777-66');

insert into PREDIO
values(1001, 1, 'CCET', '000.000.000-00', 101, 1);

insert into PREDIO
values(1002, 1, 'CLA', '000.000.000-00', 201, 1);

insert into PREDIO
values(1003, 1, 'CCH', '000.000.000-00', 301, 1);


insert into apartamento
values(1001, 101);
insert into apartamento
values(1001, 102);
insert into apartamento
values(1001, 103);
insert into apartamento
values(1001, 104);
insert into apartamento
values(1001, 105);
insert into apartamento
values(1001, 106);
insert into apartamento
values(1001, 201);
insert into apartamento
values(1001, 202);
insert into apartamento
values(1001, 203);
insert into apartamento
values(1001, 204);
insert into apartamento
values(1001, 205);
insert into apartamento
values(1001, 206);

insert into apartamento
values(1002, 101);
insert into apartamento
values(1002, 102);
insert into apartamento
values(1002, 103);
insert into apartamento
values(1002, 104);
insert into apartamento
values(1002, 105);
insert into apartamento
values(1002, 106);
insert into apartamento
values(1002, 201);
insert into apartamento
values(1002, 202);
insert into apartamento
values(1002, 203);
insert into apartamento
values(1002, 204);
insert into apartamento
values(1002, 205);
insert into apartamento
values(1002, 206);

insert into apartamento
values(1003, 101);
insert into apartamento
values(1003, 102);
insert into apartamento
values(1003, 103);
insert into apartamento
values(1003, 104);
insert into apartamento
values(1003, 105);
insert into apartamento
values(1003, 106);
insert into apartamento
values(1003, 201);
insert into apartamento
values(1003, 202);
insert into apartamento
values(1003, 203);
insert into apartamento
values(1003, 204);
insert into apartamento
values(1003, 205);
insert into apartamento
values(1003, 206);

insert into COTA
values('999.888.777-66', 1001, 101, 100);

insert into COTA
values('999.888.777-66', 1001, 102, 50);

insert into COTA
values('123.456.789-11', 1001, 102, 50);

----------------------------------------------------------
