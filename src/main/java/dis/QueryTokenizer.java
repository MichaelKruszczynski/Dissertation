package dis;

import java.util.HashMap;
import java.util.Map;

import org.apache.maven.shared.utils.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class QueryTokenizer {
	private Map<String, String> avaliableTokens;

	public QueryTokenizer() {

	}

	private void initialize() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		MyUserPrincipal principal = (MyUserPrincipal) authentication.getPrincipal();
		Employee employee = principal.getEmployee();
		String currentPrincipalName = authentication.getName();
		avaliableTokens = new HashMap<String, String>();
		avaliableTokens.put("myName", employee.getName());
		avaliableTokens.put("myDepartment", employee.getDepartment().getName());
		avaliableTokens.put("myEmail", employee.getEmail());
		avaliableTokens.put("myEmployeeNo", "" + employee.getEmployeeNo());
		avaliableTokens.put("myId", "" + employee.getId());

	}

	public Object getToken(String token) {
		if (avaliableTokens == null) {
			initialize();

		}
		String tokenValue = avaliableTokens.get(token);
		if (StringUtils.isNumeric(tokenValue)) { // if parameter numeric convert it to numeric or query
			return Long.parseLong(tokenValue);
		} else {
			return "'" + tokenValue + "'";
		}
	}

	public String deTokenize(String query) {
		String[] split = query.split(ReportViewController.TOKEN_DELIMITER);
		String returnValue = split[0];
		for (int i = 1; i < split.length; i++) {
			String temp = split[i];
			int endIndex = temp.indexOf(" ");
			if (endIndex != -1) {
				String token = temp.substring(0, endIndex);
				Object replacement = getToken(token);
				returnValue += replacement + temp.substring(endIndex);
			} else {
				Object replacement = getToken(temp);
				returnValue += replacement;
			}
		}

		return returnValue;
	}

}
