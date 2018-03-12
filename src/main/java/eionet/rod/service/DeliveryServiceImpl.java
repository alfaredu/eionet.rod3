package eionet.rod.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.openrdf.query.TupleQueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import eionet.rod.dao.DeliveryDao;
import eionet.rod.model.Delivery;
import eionet.rod.util.exception.ServiceException;

@Service(value = "deliveryService")
@Transactional
public class DeliveryServiceImpl implements DeliveryService {
	
	@Autowired
	private DeliveryDao deliveriesDao;
	
	@Override
	public List<Delivery> getAllDelivery(String actDetailsId, String spatialId){
		
		return deliveriesDao.getAllDelivery(actDetailsId, spatialId);
	}
	@Override
	public void rollBackDeliveries() {
		deliveriesDao.rollBackDeliveries();
	}
	@Override
	public void commitDeliveries(HashMap<String, HashSet<Integer>> deliveredCountriesByObligations) {
		deliveriesDao.commitDeliveries(deliveredCountriesByObligations);
	}
	@Override
	public void backUpDeliveries() {
		deliveriesDao.backUpDeliveries();
	}
	@Override
	public int saveDeliveries(TupleQueryResult bindings, HashMap<String, HashSet<Integer>> savedCountriesByObligationId) throws ServiceException {
		return deliveriesDao.saveDeliveries(bindings, savedCountriesByObligationId);
	}
}
