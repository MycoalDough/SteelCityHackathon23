using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Microspace : MonoBehaviour
{
  public float lifeTime = 10;
  public float currentLife = 0;
  public Microspace ms;
  public float speed;
  private Vector3 desiredPos;
  public float moveDir = 5;
  private float yPos;
  
  private bool canMove = false;
  
  void Start()
    {
        yPos = 1.2f;
        desiredPos = new Vector3(transform.position.x + Random.Range(-moveDir, moveDir), yPos, transform.position.z + Random.Range(-moveDir, moveDir));

    }

    // Update is called once per frame
    void Update()
    {
      if(canMove){
        transform.position = Vector3.Lerp(transform.position, desiredPos, speed * Time.deltaTime);

        if (Vector3.Distance(transform.position, desiredPos) > 0.5f) return;

        desiredPos = new Vector3(transform.position.x + Random.Range(-moveDir, moveDir), yPos, transform.position.z + Random.Range(-moveDir, moveDir));
        transform.LookAt(desiredPos);
        transform.eulerAngles = new Vector3(90f, transform.rotation.y, transform.rotation.z);
      }
      
      currentLife += Time.deltaTime;
      
      if(currentLife > lifeTime)
      {
        Destroy(gameObject);
      }
    }
    
    
    void OnTriggerEnter(Collider col){
      if(col.gameObject.tag == "BacteriaSystem"){
        desiredPos = col.gameObject.transform.position; //give all inside bacteria this tag
      }
    }
    
    void OnCollisionEnter(Collider col){
      if(col.gameObject.tag == "BacteriaSystem"){
        ms.allBacteria.Remove(col.gameObject.GetComponent<BacteriaField_RLAgent02>());
        canMove = false;
        col.gameObject.GetComponent<BacteriaField_RLAgent02>().enabled =false;
        StartCourintine(killBacteria(col.gameObject));
        
      }
    }
    
    IENumerator killBacteria(GameObject bacteria){
      yield return new WaitForSeconds(5);
      Destroy(bacteria);
      canMove = true;
      
      
    }
}
