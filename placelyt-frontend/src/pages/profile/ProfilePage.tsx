import { useNavigate } from "react-router-dom";
import { logout } from "../../services/auth/authService";

function ProfilePage() {
  const navigate = useNavigate();

  const handleLogout = (): void => {
    logout();
    navigate("/login");
  };

  return (
    <div>
      <h1>Profile Page</h1>

      <button onClick={handleLogout}>
        Logout
      </button>
    </div>
  );
}

export default ProfilePage;