package ca.mcgill.ecse321.rideshare9.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ca.mcgill.ecse321.rideshare9.entity.ActiveAdvertisement;
import ca.mcgill.ecse321.rideshare9.entity.Advertisement;
import ca.mcgill.ecse321.rideshare9.entity.TripStatus;
import ca.mcgill.ecse321.rideshare9.entity.User;
import ca.mcgill.ecse321.rideshare9.entity.UserStatus;
import ca.mcgill.ecse321.rideshare9.entity.helper.AdvBestQuery;
import ca.mcgill.ecse321.rideshare9.entity.helper.AdvQuery;
import ca.mcgill.ecse321.rideshare9.entity.helper.AdvResponse;
import ca.mcgill.ecse321.rideshare9.entity.helper.RouteBestQuery;
import ca.mcgill.ecse321.rideshare9.repository.AdvertisementRepository;
import ca.mcgill.ecse321.rideshare9.repository.MapperUserAdvRepository;
import ca.mcgill.ecse321.rideshare9.service.UserService;


/**
 * MORE METHODS WANTED!! 
 * i think we need a new method that adds/deletes/changes stop/vehicles on an advertisement
 * @author yuxiangma
 */

/**
 * Notes for front end: Searching
 * --------------------------------------------------------------------------------------------------------------
 * Essential Query: 4 must be present 
 * --------------------------------------------------------------------------------------------------------------
 * destination: 			| startLocation: 				| startTimeLow: 			| startTimeHigh: 			
 * --------------------------------------------------------------------------------------------------------------
 * Optional Query: any one, or both, or neither present 
 * --------------------------------------------------------------------------------------------------------------
 * Car type: 			    | Car color: 				    | 
 * --------------------------------------------------------------------------------------------------------------
 * Order by: Option, choose 1
 * --------------------------------------------------------------------------------------------------------------
 * Price					| StartTime 					|
 * --------------------------------------------------------------------------------------------------------------
 */
@CrossOrigin
@RestController
@RequestMapping("/adv")
public class AdvertisementController {
	
	@Autowired
	private AdvertisementRepository advService;
	@Autowired
	private UserService userv; 
	@Autowired
	private MapperUserAdvRepository mserv; 

	
    /**
     * driver: create advertisement
     * Core API endpoint: Driver-1.1, Driver-1.2, Driver-1.3, Driver-1.4 in README.md at Mark branch
     * @param adv (JSON)
     */
    @PreAuthorize("hasRole('DRIVER') or hasRole('BOSSLI')")
    @RequestMapping(value = "/create-adv", method=RequestMethod.POST)
    public Advertisement postAdv(@RequestBody Advertisement adv) {

    	String currentUserName = null; 
   	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        currentUserName = authentication.getName();
        if (userv.loadUserByUsername(currentUserName) != null) {
                return advService.createAdv(adv.getTitle(), adv.getStartTime(), adv.getStartLocation(), adv.getSeatAvailable(), adv.getStops(), adv.getVehicle(), userv.loadUserByUsername(currentUserName).getId(), adv.getEndLocation());
    	} else {
    		return null; 
        }

    }
    /**
     * show all advertisement a logged in driver posted
     * @return List
     */
    @PreAuthorize("hasRole('DRIVER') or hasRole('BOSSLI')")
    @RequestMapping(value = "/get-logged-adv", method=RequestMethod.GET)
    public List<Advertisement> myAdv() {    	
    	String currentUserName = null; 
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        currentUserName = authentication.getName();
        if (userv.loadUserByUsername(currentUserName) != null) {
                return advService.findAllAdv(userv.loadUserByUsername(currentUserName).getId());
    	} else {
    		return null; 
    	}
    }
    
    /**
     * show all advertisement a logged in driver posted
     * @return List
     */
    @PreAuthorize("hasRole('DRIVER') or hasRole('BOSSLI')")
    @RequestMapping(value = "/get-logged-adv-count", method=RequestMethod.GET)
    public String myAdvCount() {    	
    	String currentUserName = null; 
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        currentUserName = authentication.getName();
        if (userv.loadUserByUsername(currentUserName) != null) {
                return advService.findAllAdvCount(userv.loadUserByUsername(currentUserName).getId());
    	} else {
    		return null; 
    	}
    }
    
    /**
     * driver: delete advertisement, but only id needed
     * @param adv (JSON)
     * @return deleted adv
     */
    @PreAuthorize("hasRole('DRIVER') or hasRole('BOSSLI')")
    @RequestMapping(value = "/delete-adv", method=RequestMethod.POST)
    public void delAdv(@RequestBody Advertisement adv) {
    	for (Advertisement a: this.myAdv()) {
    		if (a.getId() == adv.getId()) {
    			advService.removeAdv(adv.getId()); 
    		}
    	}
    }
    
    
    /**
     * driver: change advertisement content
     * @param adv (JSON)
     * @return changed advertisement
     */
    @PreAuthorize("hasRole('DRIVER') or hasRole('BOSSLI')")
    @RequestMapping(value = "/update-adv", method=RequestMethod.PUT)
    public Advertisement changeAdv(@RequestBody Advertisement adv) {
    	
    	String currentUserName = null; 
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    currentUserName = authentication.getName();
        User curr = userv.loadUserByUsername(currentUserName);
	    if (curr.getId() != advService.findAdv(adv.getId()).getDriver()) {
    		return new Advertisement(); 
    	}
    	
    	Advertisement oldadv = advService.findAdv(adv.getId()); 
    	if (adv.getTitle() != null && !adv.getTitle().isEmpty() && !adv.getTitle().equals(oldadv.getTitle())) {
    		oldadv.setTitle(adv.getTitle());
    	}
    	if (adv.getStartTime() != null && !adv.getStartTime().equals(oldadv.getStartTime())) {
    		oldadv.setStartTime(adv.getStartTime());
    	}
    	if (adv.getStartLocation() != null && !adv.getStartLocation().isEmpty() && !adv.getStartLocation().equals(oldadv.getStartLocation())) {
    		oldadv.setStartLocation(adv.getStartLocation());
    	}
	if (adv.getVehicle() > 0 && (oldadv.getVehicle() != adv.getVehicle())) {
    		oldadv.setVehicle(adv.getVehicle());
    	}
    	if (adv.getStatus() == null) {
    		oldadv.setStops(adv.getStops()); 
    		oldadv.setEndLocation(adv.getEndLocation()); 
    		return advService.updateAdv(oldadv); 
    	}
    	if (adv.getStatus() != null && adv.getStatus() != oldadv.getStatus()) {
    		if ((oldadv.getStatus() == TripStatus.REGISTERING || oldadv.getStatus() == TripStatus.CLOSED) && adv.getStatus() != TripStatus.COMPLETE) {
    			oldadv.setStatus(adv.getStatus());
			if (adv.getStatus() == TripStatus.ON_RIDE) {
    				mserv.changeUserStatusByAdv(oldadv.getId(), UserStatus.ON_RIDE); 
    			}
    		} else if (oldadv.getStatus() == TripStatus.ON_RIDE && adv.getStatus() == TripStatus.COMPLETE) {
    			oldadv.setStatus(adv.getStatus());
			mserv.changeUserStatusByAdv(oldadv.getId(), UserStatus.STANDBY);
    		}
    	}
	if (adv.getVehicle() > 0 && adv.getStatus() != TripStatus.REGISTERING) {
    		return oldadv; 
    	}
    	return advService.updateAdv(oldadv); 
    }
    
    /**
     * All user: list all advertisement
     * @param void
     * @return list of all advertisements
     */
    @PreAuthorize("hasRole('PASSENGER') or hasRole('DRIVER') or hasRole('ADMIN') or hasRole('BOSSLI')")
    @GetMapping("/get-list-adv")
    public List<Advertisement> searchAllAdv() {
        return advService.findAllAdv();
    }
    
    //@PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/active-advertisements")
    public List<ActiveAdvertisement> getActiveAdvertisements(){
    	return advService.findActiveAdvertisements();
    }
    
    /**
     * All people: list all advertisement SELECT COUNT(*) FROM tb_adv WHERE role = 'ROLE_DRIVER' GROUPBY driver ORDERED BY COUNT(*) DESC
     * Core API endpoint: Admin-2 in README.md at Mark branch: TOP DRIVER
     * @param void
     * @return list of all advertisements
     */
//    @PreAuthorize("hasRole('PASSENGER') or hasRole('DRIVER') or hasRole('ADMIN') or hasRole('BOSSLI')")
    @PostMapping("/get-top-drivers")
    public List<AdvBestQuery> getTopDriver(@RequestBody AdvQuery advCriteria) {
        return advService.findBestDriver(advCriteria); 
    }
    /**
     * All people: list all advertisement SELECT COUNT(*) FROM tb_adv WHERE role = 'ROLE_DRIVER' GROUPBY driver ORDERED BY COUNT(*) DESC
     * Core API endpoint: Admin-2 in README.md at Mark branch: TOP DRIVER
     * @param void
     * @return list of all advertisements
     */
//    @PreAuthorize("hasRole('PASSENGER') or hasRole('DRIVER') or hasRole('ADMIN') or hasRole('BOSSLI')")
    @PostMapping("/get-top-adv")
    public List<RouteBestQuery> getTopRoute(@RequestBody AdvQuery advCriteria) {
        return advService.findAllBestJourney(advCriteria);
    }
    /**
     * All user: search advertisement by carrier json
     * @param must have start, stop, start time range x, start time range y; color, model are optional; default sort by price
     * @return list of all advertisements
     */
    @PreAuthorize("hasRole('PASSENGER') or hasRole('DRIVER') or hasRole('ADMIN') or hasRole('BOSSLI')")
    @PostMapping("/get-adv-search")
    public List<AdvResponse> searchAdv(@RequestBody AdvQuery advCriteria) {
    	if (advCriteria.isSortByPrice()) {
    		if (advCriteria.getvColor() != null && advCriteria.getvModel() != null) {
    			return advService.findAdvByCriteriaAndModelAndColorSortByPrice(advCriteria); 
    		} else if (advCriteria.getvColor() != null && advCriteria.getvModel() == null) {
    			return advService.findAdvByCriteriaAndColorSortByPrice(advCriteria); 
    		} else if (advCriteria.getvColor() == null && advCriteria.getvModel() != null) {
    			return advService.findAdvByCriteriaAndModelSortByPrice(advCriteria); 
    		} else {
    			return advService.findAdvByCriteriaSortByPrice(advCriteria); 
    		}
    	} else {
    		if (advCriteria.getvColor() != null && advCriteria.getvModel() != null) {
    			return advService.findAdvByCriteriaAndModelAndColorSortByTime(advCriteria); 
    		} else if (advCriteria.getvColor() != null && advCriteria.getvModel() == null) {
    			return advService.findAdvByCriteriaAndColorSortByTime(advCriteria); 
    		} else if (advCriteria.getvColor() == null && advCriteria.getvModel() != null) {
    			return advService.findAdvByCriteriaAndModelSortByTime(advCriteria); 
    		} else {
    			return advService.findAdvByCriteriaSortByTime(advCriteria); 
    		}
    	}
    }
    @PreAuthorize("hasRole('DRIVER') or hasRole('PASSENGER') or hasRole('BOSSLI') or hasRole('ADMIN')")
	@GetMapping(value = "/get-by-id/{id}")
	public Advertisement getAdvById(@PathVariable(value = "id") long id) {
		return advService.findAdv(id);
	}
    @PreAuthorize("hasRole('BOSSLI') or hasRole('ADMIN')")
	@GetMapping(value = "/get-active-adv/{name}")
	public List<Advertisement> getAdvActive(@PathVariable(value = "name") String name) {
		return advService.findAllActiveJourney(name);
	}
}
