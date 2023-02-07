/* TEST PROCEDURE SQL */

	/*** Procedura: "InserimentoAppuntamenti"    */
        
		/* Dentro fascia - Sovrapposizione in mezzo (ad appuntamento già esistente) */
		set @outcome = '0';
		call mydoctor.InserimentoAppuntamenti('2023-01-29', '10:10', '10:20', 3, 51, 1, @outcome);
		select @outcome;

		/* Dentro fascia - Sovrapposizione all'inizio (di un appuntamento già esistente) */
		set @outcome = '0';
		call mydoctor.InserimentoAppuntamenti('2023-01-29', '09:50', '10:20', 3, 51, 1, @outcome);
		select @outcome;

		/* Dentro fascia - Sovrapposizione dopo (un appuntamento già esistente) */
		set @outcome = '0';
		call mydoctor.InserimentoAppuntamenti('2023-01-29', '10:50', '11:20', 3, 51, 1, @outcome);
		select @outcome;

		/* Dentro fascia - Coincidenza di orari (con un appuntamento già esistente) */
		set @outcome = '0';
		call mydoctor.InserimentoAppuntamenti('2023-01-29', '10:50', '11:20', 3, 51, 1, @outcome);
		select @outcome;
        

	/*** Procedura: "InserimentoDisponibilita"    */

		/* Orari conincidenti*/
		set @outcome = '0';
		call mydoctor.InserimentoDisponibilita('2023-01-29', '10:00', '18:00', 2, 20, @outcome);
		select @outcome;

		/* prima*/
		set @outcome = '0';
		call mydoctor.InserimentoDisponibilita('2023-01-29', '09:00', '18:00', 2, 20, @outcome);
		select @outcome;

		/* dopo*/
		set @outcome = '0';
		call mydoctor.InserimentoDisponibilita('2023-01-29', '15:00', '19:00', 2, 20, @outcome);
		select @outcome;

		/* coincidente*/
		set @outcome = '0';
		call mydoctor.InserimentoDisponibilita('2023-01-29', '10:00', '18:00', 2, 20, @outcome);
		select @outcome;


	/*** Procedura: "UpdateAppuntamento"  */

		/* Update al di fuori della fascia oraria*/
		set @outcome = '0';
		call mydoctor.UpdateAppuntamento('2023-01-29', '17:30', '18:30', 51, 37, @outcome);
		select @outcome;

	/* Update con sovrapposizione con altri appuntamenti*/
		set @outcome = '0';
		call mydoctor.UpdateAppuntamento('2023-01-29', '10:30', '11:30', 51, 37, @outcome);
		select @outcome;


	/*** Procedura: "UpdateDisponibilita" */

		/*Esclusione appuntamenti*/
		set @outcome = '0';
		call mydoctor.UpdateDisponibilita('2023-01-29', '10:00', '11:00', 51, 2, @outcome);
		select @outcome;

		/*Sovrapposizione con altre fasce*/
		set @outcome = '0';
		call mydoctor.UpdateDisponibilita('2023-01-29', '10:00', '19:00', 51, 2, @outcome);
		select @outcome;




    
