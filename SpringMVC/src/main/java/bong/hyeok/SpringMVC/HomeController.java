package bong.hyeok.SpringMVC;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import Trilateration.Point2D;
import Trilateration.Trilateration;
import dao.geofence.GeofenceDAO;
import dao.geofence.GeofenceDTO;
import dao.product.ProductDAO;
import dao.product.ProductDTO;
import dao.userLocation.UserLocationDAO;
import dao.userLocation.UserLocationDTO;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {

	// geofence 뷰가 보여주는 지오펜스 아이디
	private static final int _GEOFENCE_ID = 46032;

	// 사용자가 _TIME_OVER_GEOFENCE 시간 만큼 해당 GeofenceId에 없으면 없는 것으로 간주한다
	private static final int _TIME_OVER_GEOFENCE = 1;
	
	// DI
	@Autowired
	private GeofenceDAO geofenceDAO;
	@Autowired
	private UserLocationDAO userLocationDAO;
	@Autowired
	private ProductDAO productDAO;
	
	@Autowired 
	DataSource dataSource;
	
	
	
	
	//////////////////////////////////////////////
	// 결제
	////////////////////////////////////////////
	@RequestMapping(value="/payment")
	public String payment(Model model, @RequestParam Map<String,String> params){
		String param_stickerId = params.get("stickerId");
		String[] stickerIds = param_stickerId.split(",");
		String id = params.get("id");
		String name = params.get("name");
		ProductDTO productDTO = new ProductDTO();
		
		String product_name="";
		int price = 0;
		
		for(int i=0;i<stickerIds.length;i++)
		{
			int stickerId = Integer.parseInt(stickerIds[i]);
			
			ProductDTO product = productDAO.selectProduct(stickerId);
			if(i==0)
			{
				product_name+=product.getName();
			}
			else
			{
				product_name+=", "+product.getName();
			}
			price += product.getPrice();
		}
		
		productDTO.setName(product_name);
		productDTO.setPrice(price);
		model.addAttribute("productDTO", productDTO);	
		model.addAttribute("id",id);
		model.addAttribute("name", name);
		System.out.println(""+productDTO.getName()+","+productDTO.getPrice()+id+name);
		
		return "payment";
	}
	
	///////////////////////////////////////////////
	// 지오펜스 확인
	///////////////////////////////////////////////
	@RequestMapping(value="/geofence")
	public String geofence(Model model)
	{
		GeofenceDTO geofenceDTO = geofenceDAO.selectGeofence(_GEOFENCE_ID);
		model.addAttribute("geofenceDTO", geofenceDTO);
		
		
		return "geofence";
	}
	
	///////////////////////////////////////////////
	// main
	///////////////////////////////////////////////
	@RequestMapping(value="/main")
	public String main(Model model)
	{
		return "main";
	}


	//////////////////////////////////////////////////////////
	// geofenceId에 있는 사용자들의 위치를 얻어온다
	/////////////////////////////////////////////////////////
	@RequestMapping(value="/getUserLocation")
	@ResponseBody
	public String getUserLocation(@RequestParam Map<String, String> params)
	{
		long geofenceId = Long.parseLong(params.get("geofenceId"));
		String userLocations="";
		
		// geofenceId에 있는 사용자 ID마다 해당 사용자의 최신 위치를 받아온다
		List<UserLocationDTO> recentUserLocationList = userLocationDAO.getRecentUsersLocation(geofenceId);
		
		// 날짜 형식
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-kk-mm-ss.SSS");
		
		List<UserLocationDTO> deleteList = new ArrayList<UserLocationDTO>();
		// 날짜가 너무 오래됬으면 제거한다
		for(UserLocationDTO recentUser : recentUserLocationList)
		{
			Date userDay=null;
			long timeDiffInSec=0;
			try 
			{
				userDay = sdf.parse(recentUser.getDate2());
				timeDiffInSec= new Date().getTime() - userDay.getTime();
			} 
			catch (ParseException e) 
			{
				e.printStackTrace();
			}
			
			
			////////////////////////////////////////////////
			// 일정시간 이상 geofenceId에 없으면 사용자는 없는 것으로 간주한다
			///////////////////////////////////////////////
			timeDiffInSec = timeDiffInSec/(1000);
			if(timeDiffInSec > _TIME_OVER_GEOFENCE)
			{
				deleteList.add(recentUser);
			}
		}
		// 제거
		recentUserLocationList.removeAll(deleteList);
		
		
		
		// 끝에 "," 안찍게 하기 위함
		int cnt = 0;
		// 위치 저장
		for(UserLocationDTO recentUser : recentUserLocationList)
		{
			if(cnt==0)
			{
				userLocations+=recentUser.getUserId()+","+recentUser.getX()+","+recentUser.getY();
			}
			else
			{
				userLocations+="&"+recentUser.getUserId()+","+recentUser.getX()+","+recentUser.getY();
			}
			cnt++;
		}
		
		// ex) userLocations = "userId1,x1,y1,userId2,x2,y2"
		return userLocations;
	}
	
	

	//////////////////////////////////////////
	// 상품 ID로 상품 정보 SELECT 후 보내줌
	/////////////////////////////////////////
	@RequestMapping(value="/selectProductInfo")
	@ResponseBody
	public String selectProductInfo(@RequestParam Map<String, String> params)
	{
		String productInfo="";
		String majors = params.get("majors");
		System.out.println("majors : "+majors);
		String[] majors_split = majors.split(",");
		
		
		for(int i=0;i<majors_split.length;i++)
		{
			ProductDTO productDTO = productDAO.selectProduct(Integer.parseInt(majors_split[i]));
			if(i==0)
			{
				productInfo+=productDTO.getName()+","+productDTO.getPrice();
			}
			else
			{
				productInfo+=","+productDTO.getName()+","+productDTO.getPrice();
			}
		}
		
		return productInfo;
	}
	
	//////////////////////////////////////////
	// 사용자 위치 DB에 저장
	/////////////////////////////////////////
	@RequestMapping(value="/insertUserLocation")
	@ResponseBody
	public String insertUserLocation(@RequestParam Map<String, String> params)
	{
		String response="INSERT:USER LOCATION: OK";
		String userId = params.get("userId");
		long id = Long.parseLong(params.get("id"));
		double userX = Double.parseDouble(params.get("userPosition").split(",")[0]);
		double userY = Double.parseDouble(params.get("userPosition").split(",")[1]);
		
		if(userId == null)
		{
			userId = "anony";
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-kk-mm-ss.SSS");
		String nowDate = sdf.format(new Date());
		
		UserLocationDTO userLocationDTO = new UserLocationDTO();
		userLocationDTO.setDate2(nowDate);
		userLocationDTO.setGeofenceId(id);
		userLocationDTO.setX(userX);
		userLocationDTO.setY(userY);
		userLocationDTO.setUserId(userId);
		userLocationDAO.insertUserLocation(userLocationDTO);
		return response;
	}
	
	
	////////////////////////////////////////////////
	// Geofence 제거
	////////////////////////////////////////////////
	@RequestMapping(value="/deleteGeofence")
	@ResponseBody
	public String deleteGeofence(@RequestParam Map<String, String> params)
	{
		String response="DELETE OK";
		long id = Long.parseLong(params.get("id"));
		geofenceDAO.deleteGeofence(id);
		return response;
	}
	
	////////////////////////////////////////////////
	// GeofenceId에 해당하는 Geofence를 구성하는 비콘들의 위치 가져오기
	////////////////////////////////////////////////
	@RequestMapping(value="/selectGeofence")
	@ResponseBody
	public String selectGeofence(@RequestParam Map<String,String> params)
	{
		String response = "";
		long id = Long.parseLong(params.get("id"));
		GeofenceDTO geofenceDTO = geofenceDAO.selectGeofence(id);
		
		
		if(geofenceDTO == null)
		{
			response="null";
		}
		else
		{
			response=geofenceDTO.getBC1X()+","+geofenceDTO.getBC1Y()
				   +","+geofenceDTO.getBC2X()+","+geofenceDTO.getBC2Y()
				   +","+geofenceDTO.getBC3X()+","+geofenceDTO.getBC3Y()
				   +","+geofenceDTO.getBC4X()+","+geofenceDTO.getBC4Y()
				   +","+geofenceDTO.getName()
				   +","+geofenceDTO.getId()
				   +","+geofenceDTO.getZONE_X1()+","+geofenceDTO.getZONE_Y1()
				   +","+geofenceDTO.getZONE_X2()+","+geofenceDTO.getZONE_Y2()
				   +","+geofenceDTO.getType();
					  
		}
		return response;
	}
	
	////////////////////////////////////////
	// geofence 추가
	///////////////////////////////////////
	@RequestMapping(value="/insertGeofence")
	@ResponseBody
	public String insertGeofence(@RequestParam Map<String,String> params)
	{
		String bcPositions = params.get("bcPositions");
		String id = params.get("id");
		String name= params.get("name");
		String type=params.get("type");
		String zonePosition = params.get("zonePosition");
		
		//////////////////////////////////////////////////
		// 기존에 있던 id에 해당하는 GEOFENCE 제거
		/////////////////////////////////////////////////
		geofenceDAO.deleteGeofence(Long.parseLong(id));
		
		
		//////////////////////////////////////////////////
		// GEOFENCE 등록
		//////////////////////////////////////////////////
		GeofenceDTO geofenceDTO = new GeofenceDTO();
		String[] splitedParams = bcPositions.split(",");
		geofenceDTO.setBC1X(Double.parseDouble(splitedParams[0]));
		geofenceDTO.setBC1Y(Double.parseDouble(splitedParams[1]));
		geofenceDTO.setBC2X(Double.parseDouble(splitedParams[2]));
		geofenceDTO.setBC2Y(Double.parseDouble(splitedParams[3]));
		geofenceDTO.setBC3X(Double.parseDouble(splitedParams[4]));
		geofenceDTO.setBC3Y(Double.parseDouble(splitedParams[5]));
		geofenceDTO.setBC4X(Double.parseDouble(splitedParams[6]));
		geofenceDTO.setBC4Y(Double.parseDouble(splitedParams[7]));
		geofenceDTO.setId(Integer.parseInt(id));
		geofenceDTO.setName(name);
		splitedParams = zonePosition.split(",");
		geofenceDTO.setZONE_X1(Double.parseDouble(splitedParams[0]));
		geofenceDTO.setZONE_Y1(Double.parseDouble(splitedParams[1]));
		geofenceDTO.setZONE_X2(Double.parseDouble(splitedParams[2]));
		geofenceDTO.setZONE_Y2(Double.parseDouble(splitedParams[3]));
		
		geofenceDTO.setType(Integer.parseInt(type));
		geofenceDAO.insertGeofence(geofenceDTO);
		
		return "GEOFENCE INSERTE OK";
	}
	
	
	
	
	
	
	
	@RequestMapping(value="/TrilExample")
	public String TrilExample()
	{
		return "TrilExample";
	}
	
	
	@RequestMapping(value="/bc1PositionApply")
	public String bc1PositionApply(Model model, @RequestParam Map<String, String> params)
	{
		Point2D bc1Position = new Point2D(0,0);
		model.addAttribute("bc1Position", bc1Position);
		return "geofence";
	}
	
	@RequestMapping(value="/bc2PositionApply")
	public String bc2PositionApply(Model model, @RequestParam Map<String, String> params)
	{
		String lengthX_s = params.get("lengthX");
		
		Point2D bc1Position = new Point2D(0,0);
		model.addAttribute("bc1Position", bc1Position);
		
		if(lengthX_s.equals(""))
		{
			return "geofence";
		}
		
		double lengthX = Double.parseDouble(lengthX_s);
		model.addAttribute("lengthX", lengthX);
		return "geofence";
	}
	
	@RequestMapping(value="/bc3PositionApply")
	public String bc3PositionApply(Model model, @RequestParam Map<String, String> params)
	{
		String lengthX_s = params.get("lengthX");
		String lengthY_s = params.get("lengthY");
		
		Point2D bc1Position = new Point2D(0,0);
		model.addAttribute("bc1Position", bc1Position);
		
		double lengthX = Double.parseDouble(lengthX_s);
		model.addAttribute("lengthX", lengthX);
		
		if(lengthY_s.equals(""))
		{
			return "geofence";
		}
		
		double lengthY = Double.parseDouble(lengthY_s);
		model.addAttribute("lengthY", lengthY);
		
		return "geofence";
	}
	
	
	@RequestMapping(value="/userPositionApply")
	public String userPositionApply(Model model, @RequestParam Map<String, String> params)
	{
		double lengthX = Double.parseDouble(params.get("lengthX"));
		double lengthY = Double.parseDouble(params.get("lengthY"));
		String distances = params.get("distances");
		Point2D bc1Position = new Point2D(0,0);
		
		////////////////////////////////////
		// 비콘에서 받은 직선거리 들
		////////////////////////////////////
		double r1 = Math.sqrt(Double.parseDouble(distances.split(",")[0]));
		double r2 = Math.sqrt(Double.parseDouble(distances.split(",")[1]));
		double r3 = Math.sqrt(Double.parseDouble(distances.split(",")[2]));
		
		
		
		Point2D beacon1 = new Point2D(0,0,r1);
		Point2D beacon2 = new Point2D(lengthX,0,r2);
		Point2D beacon3 = new Point2D(lengthX,lengthY,r3);
		Point2D userLocation = Trilateration.getTrilateration(beacon1, beacon2, beacon3);
		
		model.addAttribute("lengthX", lengthX);
		model.addAttribute("lengthY", lengthY);
		model.addAttribute("bc1Position", bc1Position);
		model.addAttribute("userLocation",userLocation);
		System.out.println(userLocation.getX()+","+userLocation.getY());
		
		return "geofence";
	}
	
	
	
	
	// getters, setters
	public DataSource getDataSource() {
		return dataSource;
	}
	public UserLocationDAO getUserLocationDAO() {
		return userLocationDAO;
	}
	public void setUserLocationDAO(UserLocationDAO userLocationDAO) {
		this.userLocationDAO = userLocationDAO;
	}
	public GeofenceDAO getGeofenceDAO() {
		return geofenceDAO;
	}
	public void setGeofenceDAO(GeofenceDAO geofenceDAO) {
		this.geofenceDAO = geofenceDAO;
	}
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public ProductDAO getProductDAO() {
		return productDAO;
	}

	public void setProductDAO(ProductDAO productDAO) {
		this.productDAO = productDAO;
	}

	public static int getGeofenceId() {
		return _GEOFENCE_ID;
	}

	public static int getTimeOverGeofence() {
		return _TIME_OVER_GEOFENCE;
	}
	
}
