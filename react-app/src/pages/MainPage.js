import logo from "../logo.svg";
import kakaoImg from "../images/kakao-login.png";

const MainPage = () => {
  return (
      <div className="App">
        <header className="App-header">
          <img src={logo} className="App-logo" alt="logo" />
          <p>
            THIS IS REACT - SPRING KAKAO LOGIN!!
          </p>
          <a href={"http://localhost:8080/oauth2/authorization/kakao"}>
            <img src={kakaoImg} alt="kakao-login"/>
          </a>
        </header>
      </div>
  )
}

export default MainPage;